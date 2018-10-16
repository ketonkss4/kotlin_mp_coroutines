package co.happybits.mpcompanion.widget

import android.app.AlarmManager
import android.app.IntentService
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import co.happybits.mpcompanion.MpCompanion
import co.happybits.mpcompanion.WIDGET_UPDATE_INTERVAL
import co.happybits.mpcompanion.authentication.dependencies.LoginManager
import co.happybits.mpcompanion.networking.ServiceClientHelper
import co.happybits.mpcompanion.widget.WidgetConfigureActivity.Companion.UNWATCHED_COUNT_KEY
import co.happybits.mpcompanion.widget.WidgetConfigureActivity.Companion.WIDGET_ID_KEY
import co.happybits.mpcompanion.widget.WidgetViewModel.Companion.CONVO_ID_KEY
import java.net.URLEncoder
import javax.inject.Inject

class WidgetService : IntentService("Widget Service") {

    @Inject lateinit var poloService: ServiceClientHelper.PoloService

    companion object {
        const val WIDGET_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE"
        const val SERVICE_ACTION = "SERVICE_ACTION"
        const val START_WIDGET_ACTION = "START_WIDGET_ACTION"
        const val UPDATE_WIDGET_ACTION = "UPDATE_WIDGET_ACTION"

        fun startRecurringWidgetUpdateService(context: Context) {
            val updateWidgetIntent = Intent(context, WidgetViewProvider::class.java)
            updateWidgetIntent.action = WIDGET_UPDATE
            updateWidgetIntent.putExtra(UPDATE_WIDGET_ACTION, UPDATE_WIDGET_ACTION)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, updateWidgetIntent, 0)
            val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val interval = (WIDGET_UPDATE_INTERVAL)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent)
        }

        fun stoptRecurringWidgetUpdateService(context: Context) {
            val updateWidgetIntent = Intent(context, WidgetViewProvider::class.java)
            updateWidgetIntent.action = WIDGET_UPDATE
            val pendingIntent = PendingIntent.getBroadcast(context, 0, updateWidgetIntent, 0)
            val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        MpCompanion.appComponent.inject(this)
    }

    override fun onHandleIntent(receivedIntent: Intent) {
        when (receivedIntent.action) {
            START_WIDGET_ACTION -> {
                val intent = Intent(WIDGET_UPDATE)
                intent.action = WIDGET_UPDATE
                intent.putExtra(START_WIDGET_ACTION, START_WIDGET_ACTION)
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