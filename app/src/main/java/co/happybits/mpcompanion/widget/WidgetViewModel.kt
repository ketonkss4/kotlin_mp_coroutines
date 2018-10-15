package co.happybits.mpcompanion.widget

import androidx.lifecycle.MutableLiveData
import co.happybits.mpcompanion.concurrency.KtDispatchers
import co.happybits.mpcompanion.data.Conversation
import co.happybits.mpcompanion.networking.ServiceClientHelper
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch

class WidgetViewModel(private val poloService: ServiceClientHelper.PoloService,
                      private val dispatchers: KtDispatchers,
                      private val poloWidgetData: MutableLiveData<PoloWidget> ) {

    companion object {
        val TARGET_ID: Int = 100
    }

    private suspend fun requestConversationData(): Conversation {
        //TODO replace test targetID with one from network
        val conversations = poloService.requestConversationSync().await()
        return conversations.first { it.conversation_id == TARGET_ID }
    }

    fun syncWidgetData() {
        GlobalScope.launch(dispatchers.ioDispatcher()) {
            val conversationData = requestConversationData()
            poloWidgetData.postValue(PoloWidget(conversationId = conversationData.conversation_id,
                    name = conversationData.title,
                    unwatchedCount = conversationData.messages.filter { it.isUnwatchted() }.count()
            ))
        }
    }
}