package co.happybits.mpcompanion.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.widget.RemoteViews
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.happybits.mpcompanion.MpCompanion
import co.happybits.mpcompanion.R
import co.happybits.mpcompanion.concurrency.KtDispatchers
import co.happybits.mpcompanion.data.Conversation
import co.happybits.mpcompanion.data.getMyUserId
import co.happybits.mpcompanion.networking.ServiceClientHelper
import co.happybits.mpcompanion.widget.WidgetService.Companion.SERVICE_ACTION
import co.happybits.mpcompanion.widget.persistence.WidgetPreferencesManager
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch

class WidgetViewModel(private val poloService: ServiceClientHelper.PoloService,
                      private val dispatchers: KtDispatchers,
                      val poloWidgetData: MutableLiveData<List<Conversation>>,
                      private val widgetPreferencesManager: WidgetPreferencesManager
) : ViewModel() {

    companion object {
        const val CONVO_ID_KEY = "CONVO_ID_KEY"
    }

    private suspend fun requestTargetConversationData(targetConversation: String): Conversation {
        val response = poloService.requestConversationSync().await()
        return response.conversations.first { it.conversation_id == targetConversation }
    }

    suspend fun syncWidgetData(targetConversation: String): PoloWidget {
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

    fun createHeartReplyPendingIntent(convoId: String) : PendingIntent {
        val context = MpCompanion.instance.applicationContext
        val intent = Intent(context, WidgetService::class.java)
        intent.action = SERVICE_ACTION
        intent.putExtra(CONVO_ID_KEY, convoId)
        return PendingIntent.getService(context, 0, intent, 0)
    }

    fun updateWidgetData(
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray,
            remoteViews: RemoteViews
    ) {
        for (appWidgetId in appWidgetIds) {
            GlobalScope.launch(dispatchers.uiDispatcher()) {
                val convoId = widgetPreferencesManager.getConvoIdPref(appWidgetId)
                convoId?.let {
                    val poloWidget = syncWidgetData(it)
                    updateWidgetView(
                            appWidgetManager,
                            appWidgetId,
                            remoteViews,
                            poloWidget.unwatchedCount,
                            it
                    )
                }

            }
        }
    }

    fun updateWidgetView(
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            views: RemoteViews,
            widgetText: String = "0",
            convoId: String
    ) {
        views.setTextViewText(R.id.appwidget_text, widgetText)
        views.setOnClickPendingIntent(R.id.appwidget_text, createHeartReplyPendingIntent(convoId))
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    fun saveConversationId(appWidgetId: Int, convoId: String){
        widgetPreferencesManager.saveConvoIdPref(appWidgetId, convoId)
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
