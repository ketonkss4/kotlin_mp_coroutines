package co.happybits.mpcompanion.widget

import android.appwidget.AppWidgetManager
import android.widget.RemoteViews
import androidx.lifecycle.MutableLiveData
import co.happybits.mpcompanion.R
import co.happybits.mpcompanion.concurrency.AppDispatchers
import co.happybits.mpcompanion.concurrency.KtDispatchers
import co.happybits.mpcompanion.data.*
import co.happybits.mpcompanion.networking.ServiceClientHelper
import co.happybits.mpcompanion.util.SingleVideoConversationData
import co.happybits.mpcompanion.util.buildTestConversation
import co.happybits.mpcompanion.widget.persistence.WidgetPreferencesManager
import com.google.gson.GsonBuilder
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Rule
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.*
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
    @Mock
    lateinit var widgetPreferences: WidgetPreferencesManager
    @InjectMocks
    lateinit var widgetViewModel: WidgetViewModel
    @Captor
    private lateinit var captor: ArgumentCaptor<List<Conversation>>

    @Test
    fun testShouldReturnSampleData_OnConversationDataRequest() {
        val response = createTestResponse()
        val deferredMock = GlobalScope.async { response }

        Mockito.`when`(dispatchers.ioDispatcher()).thenReturn(AppDispatchers().testDispatcher())

        Mockito.`when`(runBlocking { poloService.requestConversationSync() }).thenReturn(deferredMock)

        widgetViewModel.requestConversationsListData()
        verify(poloWidgetData, times(1)).postValue(captor.capture())
        val conversation = captor.value.first()
        assert(conversation.conversation_id == "100")
    }

    @Test
    fun testShouldReturnZeroCount_OnGetUnwatchedVideoCount() {
        val conversation = buildTestConversation()
        val count = conversation.getUnwatchedCount()
        assert(count == "0")
    }

    @Test
    fun testShouldReturnCount_OnGetUnwatchedVideoCount() {
        val gson = GsonBuilder()
                .registerTypeAdapter(Viewers::class.java, ViewersDeserializer())
                .create()
        val response = gson.fromJson(SingleVideoConversationData, Response::class.java)
        val conversation = response.conversations.first()
        val count = conversation.getUnwatchedCount()
        assert(count == "1")
    }

    @Test
    fun testShouldUpdateTargetWidget() {
        val targetWidgetId = 2
        val testWidgetIds = intArrayOf(1, 2, 3)
        val response = createTestResponse()
        val deferredMock = GlobalScope.async { response }
        val remoteViews = mock(RemoteViews::class.java)
        val appWidgetManager = mock(AppWidgetManager::class.java)
        Mockito.`when`(runBlocking { poloService.requestConversationSync() }).thenReturn(deferredMock)
        Mockito.`when`(dispatchers.uiDispatcher()).thenReturn(AppDispatchers().testDispatcher())
        Mockito.`when`(widgetPreferences.getConvoIdPref(targetWidgetId)).thenReturn("100")
        widgetViewModel.updateWidgetData(appWidgetManager, testWidgetIds, remoteViews)

        verify(appWidgetManager).updateAppWidget(targetWidgetId, remoteViews)
    }

    @Test
    fun testShouldUpdateTargetWidget_withTargetData() {
        val targetWidgetId = 2
        val testWidgetIds = intArrayOf(1, 2, 3)
        val gson = GsonBuilder()
                .registerTypeAdapter(Viewers::class.java, ViewersDeserializer())
                .create()
        val testResponse = gson.fromJson(SingleVideoConversationData, Response::class.java)
        val targetConversation = testResponse.conversations.first().conversation_id

        val deferredMock = GlobalScope.async { testResponse }
        val remoteViews = mock(RemoteViews::class.java)
        val appWidgetManager = mock(AppWidgetManager::class.java)
        Mockito.`when`(runBlocking { poloService.requestConversationSync() }).thenReturn(deferredMock)
        Mockito.`when`(dispatchers.uiDispatcher()).thenReturn(AppDispatchers().testDispatcher())
        Mockito.`when`(widgetPreferences.getConvoIdPref(targetWidgetId)).thenReturn(targetConversation)
        widgetViewModel.updateWidgetData(appWidgetManager, testWidgetIds, remoteViews)

        verify(remoteViews).setTextViewText(R.id.appwidget_text, "1")
        verify(appWidgetManager).updateAppWidget(targetWidgetId, remoteViews)
    }

    private fun createTestResponse(): Response {
        val testConversation = buildTestConversation(conversation_id = "100")
        val testList = arrayListOf(testConversation)
        return Response("", "", testList)
    }

}
