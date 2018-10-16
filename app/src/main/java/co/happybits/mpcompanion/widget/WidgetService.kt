package co.happybits.mpcompanion.widget

import android.app.IntentService
import android.appwidget.AppWidgetManager
import android.content.Intent
import co.happybits.mpcompanion.widget.WidgetConfigureActivity.Companion.CONVO_INTENT_KEY
import co.happybits.mpcompanion.widget.WidgetConfigureActivity.Companion.WIDGET_ID_KEY

class WidgetService : IntentService("Widget Service") {

    companion object {
        const val WIDGET_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE"
    }

    override fun onHandleIntent(recievdIntent: Intent) {
        val intent = Intent(WIDGET_UPDATE)
        intent.action = WIDGET_UPDATE
        intent.putExtra(CONVO_INTENT_KEY, recievdIntent.getStringExtra(CONVO_INTENT_KEY))
        intent.putExtra(WIDGET_ID_KEY, recievdIntent.getIntExtra(
                WIDGET_ID_KEY,
                AppWidgetManager.INVALID_APPWIDGET_ID
        ))
        sendBroadcast(intent)
    }

}