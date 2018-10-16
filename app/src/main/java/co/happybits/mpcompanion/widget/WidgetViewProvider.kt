package co.happybits.mpcompanion.widget

import android.app.AlarmManager
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import co.happybits.mpcompanion.MpCompanion
import co.happybits.mpcompanion.R
import co.happybits.mpcompanion.widget.WidgetConfigureActivity.Companion.UNWATCHED_COUNT_KEY
import co.happybits.mpcompanion.widget.WidgetConfigureActivity.Companion.WIDGET_ID_KEY
import co.happybits.mpcompanion.widget.WidgetService.Companion.START_WIDGET_ACTION
import co.happybits.mpcompanion.widget.WidgetService.Companion.UPDATE_WIDGET_ACTION
import co.happybits.mpcompanion.widget.WidgetViewModel.Companion.CONVO_ID_KEY
import co.happybits.mpcompanion.widget.dependencies.DaggerWidgetComponent
import javax.inject.Inject


/**
 * Implementation of App Widget functionality.
 */
class WidgetViewProvider : AppWidgetProvider() {
    @Inject
    lateinit var widgetViewModel: WidgetViewModel


    init {
        injectDaggerDependencies()
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        widgetViewModel.updateWidgetData(appWidgetManager, appWidgetIds, RemoteViews(context.packageName, R.layout.widget_view_controller))
    }

    override fun onReceive(context: Context, intent: Intent) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(
                context,
                WidgetViewProvider::class.java
        ))

        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            if (intent.hasExtra(START_WIDGET_ACTION)) {
                //start widget with initial data from configuration request
                val widgetText = intent.getStringExtra(UNWATCHED_COUNT_KEY)
                val convoId = intent.getStringExtra(CONVO_ID_KEY)
                val targetWidget = intent.getIntExtra(WIDGET_ID_KEY, AppWidgetManager.INVALID_APPWIDGET_ID)
                for (id in appWidgetIds) {
                    if (targetWidget == id) widgetViewModel.updateWidgetView(
                            appWidgetManager,
                            id,
                            RemoteViews(context.packageName, R.layout.widget_view_controller),
                            widgetText,
                            convoId
                    )
                }
            }

            if (intent.hasExtra(UPDATE_WIDGET_ACTION)) {
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
        }
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        if (MpCompanion.instance.isMyServiceRunning(WidgetService::class.java)) {
            val intent = Intent(context, WidgetService::class.java)
            MpCompanion.instance.stopService(intent)
            WidgetService.stoptRecurringWidgetUpdateService(context)
        }
    }

    private fun injectDaggerDependencies() {
        DaggerWidgetComponent.builder()
                .appComponent(MpCompanion.appComponent)
                .build()
                .inject(this)
    }
}

