package co.happybits.mpcompanion.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import co.happybits.mpcompanion.MpCompanion
import co.happybits.mpcompanion.R
import co.happybits.mpcompanion.authentication.AuthViewModel
import co.happybits.mpcompanion.data.Conversation
import co.happybits.mpcompanion.widget.dependencies.DaggerWidgetComponent
import javax.inject.Inject

/**
 * The configuration screen for the [WidgetViewController] AppWidget.
 */
class WidgetConfigureActivity : AppCompatActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    @BindView(R.id.conversations_list_view)
    lateinit var conversationsListView: RecyclerView
    @Inject
    lateinit var widgetViewModel: WidgetViewModel
    @Inject
    lateinit var authViewModel: AuthViewModel
    lateinit var adapter: ConversationsListAdapter

    companion object {

        private const val PREFS_NAME = "co.happybits.mpcompanion.widget.NewAppWidget"
        private const val PREF_PREFIX_KEY = "appwidget_"
        const val CONVO_INTENT_KEY = "CONVO_INTENT_KEY"
        const val WIDGET_ID_KEY = "WIDGET_ID_KEY"
        // Write the prefix to the SharedPreferences object for this widget
        internal fun saveConvoIdPref(context: Context, appWidgetId: Int, conversationId: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            prefs.putString(PREF_PREFIX_KEY + appWidgetId, conversationId)
            prefs.apply()
        }

        fun getConvoIdPref(context: Context, appWidgetId: Int) : String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(PREF_PREFIX_KEY + appWidgetId, null)
        }
    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(Activity.RESULT_CANCELED)
        setContentView(R.layout.app_widget_configure)
        // Find the widget id from the intent.
        if (checkValidWidgetId()) return
        injectDaggerDependencies()
        ButterKnife.bind(this)
        ButterKnife.setDebug(true)
        adapter = ConversationsListAdapter()
        conversationsListView.layoutManager = LinearLayoutManager(this)
        conversationsListView.adapter = adapter
        widgetViewModel.poloWidgetData.observe(this, Observer { adapter.refreshList(it) })
        adapter.selectData.observe(this, onConvoSelected())
        authViewModel.authenticateLogin()
        widgetViewModel.requestConversationsListData()
    }

    private fun onConvoSelected(): Observer<Conversation> {
        return Observer { conversation ->
            saveConvoIdPref(this, appWidgetId, conversation.conversation_id)

            val intent = Intent(this, WidgetService::class.java)
            intent.putExtra(CONVO_INTENT_KEY, widgetViewModel.getUnwatchedCount(conversation))
            intent.putExtra(WIDGET_ID_KEY, appWidgetId)
            startService(intent)

            val appWidgetManager = AppWidgetManager.getInstance(this)
            WidgetViewController.updateAppWidget(this, appWidgetManager, appWidgetId)
            completeConfiguration()
        }
    }



    private fun completeConfiguration() {
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    private fun checkValidWidgetId(): Boolean {
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return true
        }
        return false
    }

    private fun injectDaggerDependencies() {
        DaggerWidgetComponent.builder()
                .appComponent(MpCompanion.appComponent)
                .build()
                .inject(this)
    }
}

