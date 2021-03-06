package co.happybits.mpcompanion.widget

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.lifecycle.MutableLiveData
import co.happybits.mpcompanion.BuildConfig
import co.happybits.mpcompanion.MpCompanion
import co.happybits.mpcompanion.R
import co.happybits.mpcompanion.authentication.AuthViewModel
import co.happybits.mpcompanion.concurrency.CoroutineScopedViewModel
import co.happybits.mpcompanion.concurrency.KtDispatchers
import co.happybits.mpcompanion.data.Conversation
import co.happybits.mpcompanion.data.getConversationTitle
import co.happybits.mpcompanion.data.getImageUrl
import co.happybits.mpcompanion.data.getUnwatchedCount
import co.happybits.mpcompanion.networking.PoloService
import co.happybits.mpcompanion.networking.syncConversationData
import co.happybits.mpcompanion.widget.WidgetConfigureActivity.Companion.CONVO_ID_KEY
import co.happybits.mpcompanion.widget.WidgetService.Companion.SERVICE_ACTION
import co.happybits.mpcompanion.widget.persistence.WidgetPreferencesManager
import com.squareup.picasso.Picasso
import kotlinx.coroutines.experimental.launch

class WidgetViewModel(private val poloService: PoloService,
                      override val dispatchers: KtDispatchers,
                      val poloWidgetData: MutableLiveData<List<Conversation>>,
                      private val widgetPreferencesManager: WidgetPreferencesManager,
                      private val authViewModelViewModel: AuthViewModel,
                      private val picasso: Picasso
) : CoroutineScopedViewModel() {

    private suspend fun requestTargetConversationData(targetConversation: String): Conversation? {
        val response = poloService.syncConversationData()
        return response.conversations.firstOrNull { it.conversation_id == targetConversation }
    }

    private suspend fun syncWidgetData(targetConversation: String): PoloWidget? {
        val conversationData = requestTargetConversationData(targetConversation)
        conversationData?.let {
            return PoloWidget(conversationId = conversationData.conversation_id,
                    title = conversationData.getConversationTitle(),
                    unwatchedCount = conversationData.getUnwatchedCount(),
                    imgUrl = conversationData.getImageUrl(authViewModelViewModel.getAuthentication()))
        }
        return null
    }

    fun requestConversationsListData() {
        launch(dispatchers.ioDispatcher()) {
            val response = poloService.syncConversationData()
            poloWidgetData.postValue(response.conversations)
        }
    }

    private fun createHeartReplyPendingIntent(convoId: String): PendingIntent {
        val context = MpCompanion.instance.applicationContext
        val intent = Intent(context, WidgetService::class.java)
        intent.action = SERVICE_ACTION
        intent.putExtra(CONVO_ID_KEY, convoId)
        return PendingIntent.getService(context, 0, intent, FLAG_UPDATE_CURRENT)
    }

    private fun createWidgetSetupPendingIntent(widgetId: Int): PendingIntent {
        val context = MpCompanion.instance.applicationContext
        val intent = Intent(context, WidgetConfigureActivity::class.java)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        return PendingIntent.getActivity(context, 0, intent, FLAG_UPDATE_CURRENT)
    }

    fun updateWidgetData(
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray,
            remoteViews: RemoteViews
    ) {
        for (appWidgetId in appWidgetIds) {
            val convoId = widgetPreferencesManager.getConvoIdPref(appWidgetId)
            launch {
                convoId?.let {
                    val poloWidget = syncWidgetData(it)
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
            poloWidget: PoloWidget?
    ) {

        if(poloWidget ==  null) {
            resetAppWidget(appWidgetId, appWidgetManager)
            return
        }
        Log.v("DEBUGGING MP", "Update WidgetViewModel View.  Title = ${poloWidget.title}," +
                " Unwatch Count = ${poloWidget.unwatchedCount}" +
                " ID = ${poloWidget.conversationId}")

        views.setTextViewText(R.id.appwidget_text, poloWidget.unwatchedCount)
        views.setTextViewText(R.id.widget_title, poloWidget.title)
        if (poloWidget.hasImage()) {
            picasso.load(poloWidget.imgUrl).into(views, R.id.widget_image, intArrayOf(appWidgetId))
        } else {
            views.setImageViewResource(R.id.widget_image, R.color.colorPrimary)
        }


        views.setOnClickPendingIntent(
                R.id.widget_image,
                createHeartReplyPendingIntent(poloWidget.conversationId)
        )
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun resetAppWidget(appWidgetId: Int, appWidgetManager: AppWidgetManager) {
        widgetPreferencesManager.removeConvoIdPref(appWidgetId)
        setDefaultWidgetView(
                appWidgetManager,
                appWidgetId,
                RemoteViews(BuildConfig.APPLICATION_ID, R.layout.widget_setup_view)
        )
        return
    }

    fun startTrackingConversationId(appWidgetId: Int, convoId: String) {
        widgetPreferencesManager.saveConvoIdPref(appWidgetId, convoId)
    }

    fun stopTrackingConversationId(appWidgetId: Int) {
        widgetPreferencesManager.removeConvoIdPref(appWidgetId)
    }

    fun setDefaultWidgetView(
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            views: RemoteViews): Boolean {
        val convoId = widgetPreferencesManager.getConvoIdPref(appWidgetId)
        return if (convoId == null) {
            views.setOnClickPendingIntent(
                    R.id.widget_click_to_add,
                    createWidgetSetupPendingIntent(appWidgetId)
            )
            appWidgetManager.updateAppWidget(appWidgetId, views)
            true
        } else {
            false
        }
    }
}

private fun PoloWidget.hasImage(): Boolean {
    return !imgUrl.isNullOrBlank()
}
