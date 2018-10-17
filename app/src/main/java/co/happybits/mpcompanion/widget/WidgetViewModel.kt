package co.happybits.mpcompanion.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.happybits.mpcompanion.MpCompanion
import co.happybits.mpcompanion.R
import co.happybits.mpcompanion.concurrency.KtDispatchers
import co.happybits.mpcompanion.data.Conversation
import co.happybits.mpcompanion.data.getConversationTitle
import co.happybits.mpcompanion.data.getUnwatchedCount
import co.happybits.mpcompanion.networking.PoloService
import co.happybits.mpcompanion.networking.syncConversationData
import co.happybits.mpcompanion.widget.WidgetConfigureActivity.Companion.CONVO_ID_KEY
import co.happybits.mpcompanion.widget.WidgetService.Companion.SERVICE_ACTION
import co.happybits.mpcompanion.widget.persistence.WidgetPreferencesManager
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch

class WidgetViewModel(private val poloService: PoloService,
                      private val dispatchers: KtDispatchers,
                      val poloWidgetData: MutableLiveData<List<Conversation>>,
                      private val widgetPreferencesManager: WidgetPreferencesManager
) : ViewModel() {

    private suspend fun requestTargetConversationData(targetConversation: String): Conversation {
        val response = poloService.syncConversationData()
        return response.conversations.first { it.conversation_id == targetConversation }
    }

    suspend fun syncWidgetData(targetConversation: String): PoloWidget {
        val conversationData = requestTargetConversationData(targetConversation)
        return PoloWidget(conversationId = conversationData.conversation_id,
                title = conversationData.getConversationTitle(),
                unwatchedCount = conversationData.getUnwatchedCount()
        )
    }

    fun requestConversationsListData() {
        GlobalScope.launch(dispatchers.ioDispatcher()) {
            val response = poloService.syncConversationData()
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
                    Log.v("DEBUGGING MP", "Update Widget From Saved Prefs ConvoID = $it " +
                            "Response: Title = ${poloWidget.title}, Unwatch Count = ${poloWidget.unwatchedCount}")

                    updateWidgetView(
                            appWidgetManager,
                            appWidgetId,
                            remoteViews,
                            poloWidget
                    )
                }

            }
        }
    }

    fun updateWidgetView(
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            views: RemoteViews,
            poloWidget: PoloWidget
    ) {
        Log.v("DEBUGGING MP", "Update Widget View.  Title = ${poloWidget.title}," +
                " Unwatch Count = ${poloWidget.unwatchedCount}" +
                " ID = ${poloWidget.conversationId}")

        views.setTextViewText(R.id.appwidget_text, poloWidget.unwatchedCount)
        views.setTextViewText(R.id.widget_title, poloWidget.title)
        views.setOnClickPendingIntent(
                R.id.appwidget_text,
                createHeartReplyPendingIntent(poloWidget.conversationId)
        )
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    fun startTrackingConversationId(appWidgetId: Int, convoId: String){
        widgetPreferencesManager.saveConvoIdPref(appWidgetId, convoId)
    }

    fun stopTrackingConversationId(appWidgetId: Int){
        widgetPreferencesManager.removeConvoIdPref(appWidgetId)
    }
}
