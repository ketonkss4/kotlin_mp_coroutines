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

    private suspend fun requestTargetConversationData(targetConversation: String): Conversation {
        //TODO replace test targetID with one from network
        val response = poloService.requestConversationSync().await()
        return response.conversations.first { it.conversation_id == targetConversation }
    }

    suspend fun syncWidgetData(targetConversation : String): PoloWidget {
        val conversationData = requestTargetConversationData(targetConversation)
        return PoloWidget(conversationId = conversationData.conversation_id,
                name = conversationData.title,
                unwatchedCount = getUnwatchedCount(conversationData)
        )
    }

    fun requestConversationsListData() {
        GlobalScope.launch(dispatchers.ioDispatcher()) {
            val response = poloService.requestConversationSync().await()
            poloWidgetData.postValue(response.conversations)
        }
    }

    internal fun getUnwatchedCount(conversation: Conversation): String {
        val myUserId = conversation.members.getMyUserId()
        val filteredCollection = conversation.messages.entries
        return if (filteredCollection.count { it.viewers == null } > 0) {
            filteredCollection.count { it.viewers == null }.toString()
        } else {
            filteredCollection
                    .count { entries -> !entries.viewers!!.viewerIds.contains(myUserId) }
                    .toString()
        }
    }

}
