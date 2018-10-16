package co.happybits.mpcompanion.widget

import android.app.IntentService
import android.appwidget.AppWidgetManager
import android.content.Intent
import co.happybits.mpcompanion.MpCompanion
import co.happybits.mpcompanion.authentication.dependencies.LoginManager
import co.happybits.mpcompanion.injection.DaggerAppComponent
import co.happybits.mpcompanion.networking.ServiceClientHelper
import co.happybits.mpcompanion.widget.WidgetConfigureActivity.Companion.UNWATCHED_COUNT_KEY
import co.happybits.mpcompanion.widget.WidgetConfigureActivity.Companion.UPDATE_WIDGET_ACTION
import co.happybits.mpcompanion.widget.WidgetConfigureActivity.Companion.WIDGET_ID_KEY
import co.happybits.mpcompanion.widget.WidgetViewModel.Companion.CONVO_ID_KEY
import java.net.URLEncoder
import javax.inject.Inject

class WidgetService : IntentService("Widget Service") {

    @Inject lateinit var poloService : ServiceClientHelper.PoloService

    companion object {
        const val WIDGET_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE"
        const val SERVICE_ACTION = "SERVICE_ACTION"
    }

    override fun onCreate() {
        super.onCreate()
        MpCompanion.appComponent.inject(this)
    }

    override fun onHandleIntent(receivedIntent: Intent) {
        when(receivedIntent.action){
            UPDATE_WIDGET_ACTION ->  {
                val intent = Intent(WIDGET_UPDATE)
                intent.action = WIDGET_UPDATE
                intent.putExtra(
                        UNWATCHED_COUNT_KEY,
                        receivedIntent.getStringExtra(UNWATCHED_COUNT_KEY)
                )
                intent.putExtra(CONVO_ID_KEY, receivedIntent.getStringExtra(CONVO_ID_KEY))
                intent.putExtra(WIDGET_ID_KEY, receivedIntent.getIntExtra(
                        WIDGET_ID_KEY,
                        AppWidgetManager.INVALID_APPWIDGET_ID
                ))
                sendBroadcast(intent)
            }
            SERVICE_ACTION -> {
                val convoId = receivedIntent.getStringExtra(CONVO_ID_KEY)
                //TODO invalid emoji input must correct
                poloService.sendMessage(
                        convoId,
                        LoginManager.createXID(),
                        URLEncoder.encode(heartEmoji(), "UTF-8")
                )
            }
        }

    }

    private fun heartEmoji() = String(Character.toChars(0x2764))

}