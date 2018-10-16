package co.happybits.mpcompanion.widget

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.happybits.mpcompanion.concurrency.KtDispatchers
import co.happybits.mpcompanion.data.Conversation
import co.happybits.mpcompanion.data.getMyUserId
import co.happybits.mpcompanion.networking.ServiceClientHelper
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch

class WidgetViewModel(private val poloService: ServiceClientHelper.PoloService,
                      private val dispatchers: KtDispatchers,
                      val poloWidgetData: MutableLiveData<List<Conversation>>
) : ViewModel() {

    companion object {
        val TARGET_ID: String = "100"
    }

    private suspend fun requestTargetConversationData(): Conversation {
        //TODO replace test targetID with one from network
        val response = poloService.requestConversationSync().await()
        return response.conversations.first { it.conversation_id == TARGET_ID }
    }

    suspend fun syncWidgetData(): PoloWidget {
        val conversationData = requestTargetConversationData()
        val poloWidget = PoloWidget(conversationId = conversationData.conversation_id,
                name = conversationData.title,
                unwatchedCount = conversationData
                        .messages
                        .entries
                        .asSequence()
                        .filter {
                            val myUserId = conversationData.members.getMyUserId()
                            !it.viewers.viewerIds.contains(myUserId)
                        }
                        .count()
        )
        return poloWidget
    }

    fun requestConversationsListData() {
        GlobalScope.launch(dispatchers.ioDispatcher()) {
            val response = poloService.requestConversationSync().await()
            poloWidgetData.postValue(response.conversations)
        }
    }

}
