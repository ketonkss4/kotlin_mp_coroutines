package co.happybits.mpcompanion.widget

import android.app.AlarmManager
import android.app.IntentService
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import co.happybits.mpcompanion.MpCompanion
import co.happybits.mpcompanion.WIDGET_UPDATE_INTERVAL
import co.happybits.mpcompanion.authentication.dependencies.LoginManager
import co.happybits.mpcompanion.networking.PoloService
import co.happybits.mpcompanion.widget.WidgetConfigureActivity.Companion.CONVO_ID_KEY
import co.happybits.mpcompanion.widget.WidgetConfigureActivity.Companion.POLO_WIDGET_KEY
import co.happybits.mpcompanion.widget.WidgetConfigureActivity.Companion.WIDGET_ID_KEY
import javax.inject.Inject

class WidgetService : IntentService("Widget Service") {

    @Inject
    lateinit var poloService: PoloService

    companion object {
        const val SERVICE_ACTION = "SERVICE_ACTION"
        const val START_WIDGET_ACTION = "START_WIDGET_ACTION"
        const val UPDATE_WIDGET_ACTION = "UPDATE_WIDGET_ACTION"

        fun startRecurringWidgetUpdateService(context: Context) {
            val updateWidgetIntent = Intent(context, WidgetViewProvider::class.java)
            updateWidgetIntent.action = UPDATE_WIDGET_ACTION
            val pendingIntent = PendingIntent.getBroadcast(context, 0, updateWidgetIntent, 0)
            val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val interval = (WIDGET_UPDATE_INTERVAL)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent)
        }

        fun stopRecurringWidgetUpdateService(context: Context) {
            val updateWidgetIntent = Intent(context, WidgetViewProvider::class.java)
            updateWidgetIntent.action = UPDATE_WIDGET_ACTION
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
                val intent = Intent(applicationContext, WidgetViewProvider::class.java)
                intent.action = START_WIDGET_ACTION
                intent.putExtra(POLO_WIDGET_KEY, receivedIntent.getSerializableExtra(POLO_WIDGET_KEY))
                intent.putExtra(WIDGET_ID_KEY, receivedIntent.getIntExtra(
                        WIDGET_ID_KEY,
                        AppWidgetManager.INVALID_APPWIDGET_ID
                ))
                sendBroadcast(intent)
            }
            SERVICE_ACTION -> {
                val convoId = receivedIntent.getStringExtra(CONVO_ID_KEY)
                //TODO invalid emoji input must correct
                val request = poloService.sendMessage(
                        convoId,
                        LoginManager.createXID(),
                        heartEmoji()
                )
            }
        }

    }

    private fun notifyUserOfResult(resultText: String) {
        Toast.makeText(applicationContext,
                resultText,
                Toast.LENGTH_SHORT).show()
    }

    private fun heartEmoji() = "u+2764"

}