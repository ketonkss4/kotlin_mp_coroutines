package co.happybits.mpcompanion.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import co.happybits.mpcompanion.MpCompanion
import co.happybits.mpcompanion.R
import co.happybits.mpcompanion.widget.WidgetConfigureActivity.Companion.CONVO_INTENT_KEY
import co.happybits.mpcompanion.widget.WidgetConfigureActivity.Companion.WIDGET_ID_KEY
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
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, WidgetViewProvider::class.java))

        //update the specific widget specified in intent
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE && intent.hasExtra(CONVO_INTENT_KEY)) {
            val widgetText = intent.getStringExtra(CONVO_INTENT_KEY)
            val targetWidget = intent.getIntExtra(WIDGET_ID_KEY, AppWidgetManager.INVALID_APPWIDGET_ID)
            for (id in appWidgetIds) {
                if (targetWidget == id) widgetViewModel.updateWidgetView(
                        appWidgetManager,
                        id,
                        RemoteViews(context.packageName, R.layout.widget_view_controller),
                        widgetText
                )
            }
        }
    }

    private fun injectDaggerDependencies() {
        DaggerWidgetComponent.builder()
                .appComponent(MpCompanion.appComponent)
                .build()
                .inject(this)
    }
}
