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
import co.happybits.mpcompanion.widget.dependencies.DaggerWidgetComponent
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject


/**
 * Implementation of App Widget functionality.
 */
class WidgetViewController : AppWidgetProvider() {
    @Inject
    lateinit var widgetViewModel: WidgetViewModel


    init {
        injectDaggerDependencies()
    }

    companion object {
        private var widgetText = "0"
        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.widget_view_controller)
            views.setTextViewText(R.id.appwidget_text, widgetText)
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            GlobalScope.launch(Dispatchers.Main) {
                val poloWidget = widgetViewModel.syncWidgetData()
                widgetText = poloWidget.unwatchedCount
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, WidgetViewController::class.java))
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE && intent.hasExtra(CONVO_INTENT_KEY)) {
            widgetText = intent.getStringExtra(CONVO_INTENT_KEY)
            for (id in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, id)
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

