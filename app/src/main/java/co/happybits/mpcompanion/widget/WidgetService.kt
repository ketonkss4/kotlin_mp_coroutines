package co.happybits.mpcompanion.widget

import android.app.IntentService
import android.content.Intent
import co.happybits.mpcompanion.widget.WidgetConfigureActivity.Companion.CONVO_INTENT_KEY

class WidgetService : IntentService("Widget Service") {

    companion object {
        const val WIDGET_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE"
    }

    override fun onHandleIntent(recievdIntent: Intent) {
        val intent = Intent(WIDGET_UPDATE)
        intent.action = WIDGET_UPDATE
        intent.putExtra(CONVO_INTENT_KEY, recievdIntent.getStringExtra(CONVO_INTENT_KEY))
        sendBroadcast(intent)
    }

}