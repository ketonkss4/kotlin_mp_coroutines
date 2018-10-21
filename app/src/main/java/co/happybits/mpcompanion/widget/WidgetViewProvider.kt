package co.happybits.mpcompanion.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import co.happybits.mpcompanion.MpCompanion
import co.happybits.mpcompanion.R
import co.happybits.mpcompanion.widget.WidgetConfigureActivity.Companion.POLO_WIDGET_KEY
import co.happybits.mpcompanion.widget.WidgetConfigureActivity.Companion.WIDGET_ID_KEY
import co.happybits.mpcompanion.widget.WidgetService.Companion.START_WIDGET_ACTION
import co.happybits.mpcompanion.widget.WidgetService.Companion.UPDATE_WIDGET_ACTION
import co.happybits.mpcompanion.widget.dependencies.DaggerWidgetComponent
import javax.inject.Inject


/**
 * Implementation of App WidgetViewModel functionality.
 */
class WidgetViewProvider : AppWidgetProvider() {
    @Inject
    lateinit var widgetViewModel: WidgetViewModel


    init {
        injectDaggerDependencies()
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        widgetViewModel.updateWidgetData(
                appWidgetManager,
                appWidgetIds,
                RemoteViews(context.packageName, R.layout.widget_view)
        )
    }

    override fun onReceive(context: Context, intent: Intent) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(
                context,
                WidgetViewProvider::class.java
        ))
        when (intent.action) {
            START_WIDGET_ACTION -> {
                Log.v("DEBUGGING MP", "WidgetViewModel Start Action! Count = ${appWidgetIds.count()}")
                //start widget with initial data from configuration request
                val poloWidget = intent.getSerializableExtra(POLO_WIDGET_KEY) as PoloWidget
                val targetWidget = intent.getIntExtra(
                        WIDGET_ID_KEY,
                        AppWidgetManager.INVALID_APPWIDGET_ID
                )
                for (id in appWidgetIds) {
                    if (targetWidget == id) widgetViewModel.updateWidgetView(
                            appWidgetManager,
                            id,
                            RemoteViews(context.packageName, R.layout.widget_view),
                            poloWidget
                    )
                }
            }

            UPDATE_WIDGET_ACTION -> {
                Log.v("DEBUGGING MP", "WidgetViewModel Service Update Action")
                //update widgets on regular interval
                onUpdate(context, appWidgetManager, appWidgetIds)
            }

            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                for (id in appWidgetIds) {
                    val isDefaultView = widgetViewModel.setDefaultWidgetView(
                            appWidgetManager,
                            id,
                            RemoteViews(context.packageName, R.layout.widget_setup_view)
                    )
                    if (!isDefaultView) {
                        onUpdate(context, appWidgetManager, appWidgetIds)
                    }
                }
            }
            //due to overriding onReceive must handle widget intents manually
            AppWidgetManager.ACTION_APPWIDGET_ENABLED,
            AppWidgetManager.ACTION_APPWIDGET_DISABLED,
            AppWidgetManager.ACTION_APPWIDGET_DELETED -> super.onReceive(context, intent)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Log.v("DEBUGGING MP", "WidgetViewModel Enabled!")

        WidgetService.startRecurringWidgetUpdateService(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        Log.v("DEBUGGING MP", "WidgetViewModel Deleted: ${appWidgetIds.count()}")
        appWidgetIds.forEach { widgetViewModel.stopTrackingConversationId(it) }
    }

    override fun onDisabled(context: Context) {
        Log.v("DEBUGGING MP", "Widgets Disabled")
        super.onDisabled(context)
        WidgetService.stopRecurringWidgetUpdateService(context)
        if (MpCompanion.instance.isMyServiceRunning(WidgetService::class.java)) {
            val intent = Intent(context, WidgetService::class.java)
            MpCompanion.instance.stopService(intent)
        }
    }

    private fun injectDaggerDependencies() {
        DaggerWidgetComponent.builder()
                .appComponent(MpCompanion.appComponent)
                .build()
                .inject(this)
    }
}

