package co.happybits.mpcompanion.widget

import androidx.lifecycle.MutableLiveData
import co.happybits.mpcompanion.concurrency.AppDispatchers
import co.happybits.mpcompanion.concurrency.KtDispatchers
import co.happybits.mpcompanion.data.Conversation
import co.happybits.mpcompanion.data.Response
import co.happybits.mpcompanion.data.Viewers
import co.happybits.mpcompanion.data.ViewersDeserializer
import co.happybits.mpcompanion.networking.ServiceClientHelper
import co.happybits.mpcompanion.util.SingleVideoConversationData
import co.happybits.mpcompanion.util.buildTestConversation
import com.google.gson.GsonBuilder
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Rule
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit


class WidgetViewModelTest {
    @get:Rule
    val mockitoRule = MockitoJUnit.rule()
    @Mock
    lateinit var poloService: ServiceClientHelper.PoloService
    @Mock
    lateinit var dispatchers: KtDispatchers
    @Mock
    lateinit var poloWidgetData: MutableLiveData<List<Conversation>>
    @InjectMocks
    lateinit var widgetViewModel: WidgetViewModel
    @Captor
    private lateinit var captor: ArgumentCaptor<List<Conversation>>

    @Test
    fun testShouldReturnSampleData_OnConversationDataRequest() {
        val testConversation = buildTestConversation(conversation_id = "100")
        val testList = arrayListOf(testConversation)
        val response = Response("", "", testList)
        val deferredMock = GlobalScope.async { response }

        Mockito.`when`(dispatchers.ioDispatcher()).thenReturn(AppDispatchers().testDispatcher())

        Mockito.`when`(runBlocking { poloService.requestConversationSync() }).thenReturn(deferredMock)

        widgetViewModel.requestConversationsListData()
        verify(poloWidgetData, times(1)).postValue(captor.capture())
        val conversation = captor.value.first()
        assert(conversation.conversation_id == "100")
    }

    @Test
    fun testShouldReturnZeroCount_OnGetUnwatchedVideoCount(){
        val conversation = buildTestConversation()
        val count = widgetViewModel.getUnwatchedCount(conversation)
        assert(count == "0")
    }

    @Test
    fun testShouldReturnCount_OnGetUnwatchedVideoCount(){
        val gson = GsonBuilder()
                .registerTypeAdapter(Viewers::class.java, ViewersDeserializer())
                .create()
        val response = gson.fromJson(SingleVideoConversationData, Response::class.java)
        val conversation = response.conversations.first()
        val count = widgetViewModel.getUnwatchedCount(conversation)
        assert(count == "1")
    }

}