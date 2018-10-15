package co.happybits.mpcompanion.widget

import androidx.lifecycle.MutableLiveData
import co.happybits.mpcompanion.concurrency.AppDispatchers
import co.happybits.mpcompanion.concurrency.KtDispatchers
import co.happybits.mpcompanion.networking.ServiceClientHelper
import co.happybits.mpcompanion.util.buildTestConversation
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit


class WidgetViewModelTest {
    @get:Rule val mockitoRule = MockitoJUnit.rule()
    @Mock lateinit var poloService: ServiceClientHelper.PoloService
    @Mock lateinit var dispatchers: KtDispatchers
    @Mock lateinit var poloWidgetData: MutableLiveData<PoloWidget>
    @InjectMocks lateinit var widgetViewModel: WidgetViewModel

    @Test
    fun testConversationDataRequest(){
        val testConversation = buildTestConversation(conversation_id = 100)
        val testList = arrayListOf(testConversation)
        val deferredMock = GlobalScope.async { testList }

        Mockito.`when`(dispatchers.ioDispatcher()).thenReturn(AppDispatchers().testDispatcher())

        Mockito.`when`(runBlocking { poloService.requestConversationSync() }).thenReturn(deferredMock)

        widgetViewModel.syncWidgetData()

        val argumentCaptor = ArgumentCaptor.forClass(PoloWidget::class.java)
        verify(poloWidgetData, times(1)).postValue(argumentCaptor.capture())
        val poloWidget = argumentCaptor.value
        assert(poloWidget.conversationId == 100)
    }


}