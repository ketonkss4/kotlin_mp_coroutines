package co.happybits.mpcompanion.networking

import android.util.Log
import co.happybits.mpcompanion.data.Response
import kotlinx.coroutines.experimental.Deferred
import okhttp3.ResponseBody
import retrofit2.http.*

interface PoloService {
    @GET("conversations/sync")
    fun requestConversationSync(): Deferred<Response>

    @GET("conversations/sync")
    fun continueConversationSync(@Query("t") syncId: String): Deferred<Response>

    @PUT("conversations/{conversation_id}/messages/{message_id}")
    fun sendMessage(
            @Path("conversation_id") conversationId: String,
            @Path("message_id") messageId: String,
            @Body emotiveMessage : String
    ) : Deferred<ResponseBody>
}

suspend fun PoloService.syncConversationData() : Response {
    var response = requestConversationSync().await()
    var hasNext = response.`continue`
    var syncId = response.sync

    Log.v("DEBUGGING MP", "Convo hasNex = $hasNext")
    while (hasNext) {
        val continuedResponse = continueConversationSync(syncId).await()
        hasNext = continuedResponse.`continue`
        syncId = continuedResponse.sync
        if(continuedResponse.conversations.isNotEmpty()) response = continuedResponse
//        continuedResponse.conversations.forEachIndexed { index, conversation ->
//            val additionalEntries = conversation.messages.entries
//            response.conversations[index].messages.entries.addAll(additionalEntries)
//        }
    }
    response.conversations.forEach {
        Log.v("DEBUGGING MP", "Add additional videos ConvoId = ${it.conversation_id}  " +
            "Video Count = ${it.messages.entries.count()}")
    }
    return response
}