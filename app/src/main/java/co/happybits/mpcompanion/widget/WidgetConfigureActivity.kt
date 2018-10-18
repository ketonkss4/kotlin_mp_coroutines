package co.happybits.mpcompanion.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import co.happybits.mpcompanion.MpCompanion
import co.happybits.mpcompanion.R
import co.happybits.mpcompanion.authentication.AuthViewModel
import co.happybits.mpcompanion.authentication.dependencies.persistence.hasSavedAuth
import co.happybits.mpcompanion.data.Conversation
import co.happybits.mpcompanion.data.getConversationTitle
import co.happybits.mpcompanion.data.getImageUrl
import co.happybits.mpcompanion.data.getUnwatchedCount
import co.happybits.mpcompanion.widget.WidgetService.Companion.START_WIDGET_ACTION
import co.happybits.mpcompanion.widget.dependencies.DaggerWidgetComponent
import javax.inject.Inject

/**
 * The configuration screen for the [WidgetViewProvider] AppWidget.
 */
class WidgetConfigureActivity : AppCompatActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    @BindView(R.id.conversations_list_view)
    lateinit var conversationsListView: RecyclerView
    @BindView(R.id.progress_bar)
    lateinit var progressIndicator: ProgressBar
    @BindView(R.id.editText)
    lateinit var phoneNumberInput: EditText
    @BindView(R.id.button)
    lateinit var authenticateButton: Button
    @BindView(R.id.authentication_view)
    lateinit var authenticationView: View
    @BindView(R.id.widget_selection_view)
    lateinit var widgetSelectionView: View
    @Inject
    lateinit var widgetViewModel: WidgetViewModel
    @Inject
    lateinit var authViewModel: AuthViewModel
    private lateinit var adapter: ConversationsListAdapter

    companion object {
        const val POLO_WIDGET_KEY = "POLO_WIDGET_KEY"
        const val CONVO_ID_KEY = "CONVO_ID_KEY"
        const val WIDGET_ID_KEY = "WIDGET_ID_KEY"
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
        widgetViewModel.poloWidgetData.observe(this, Observer {
            adapter.refreshList(it)
            progressIndicator.visibility = GONE
        })
        adapter.selectData.observe(this, onConvoSelected())
        authenticationView.visibility = GONE

        if (!authViewModel.authPrefs.hasSavedAuth()) {
            displayUserAuthetication()
        } else {
            widgetViewModel.requestConversationsListData()
        }
    }

    private fun displayUserAuthetication() {
        authenticationView.visibility = VISIBLE
        widgetSelectionView.visibility = GONE
        phoneNumberInput.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        if (authViewModel.authPrefs.hasSavedAuth()) {
            phoneNumberInput.setText(authViewModel.authPrefs.getPhoneAuthPref())
            authenticateButton.text = "Re-Autheticate"
        }
        authenticateButton.setOnClickListener {
            val number = phoneNumberInput.text.toString()
            authViewModel.onWidgetTriggeredAuthentication(
                    this,
                    number
            ) { displayWidgetSelectionView() }
        }
    }

    private fun displayWidgetSelectionView() {
        authenticationView.visibility = GONE
        widgetSelectionView.visibility = VISIBLE

        widgetViewModel.requestConversationsListData()
    }

    private fun onConvoSelected(): Observer<Conversation> {
        return Observer { conversation ->
            //save convo id to update widget periodically later
            widgetViewModel.startTrackingConversationId(
                    appWidgetId,
                    conversation.conversation_id
            )
            val poloWidget = PoloWidget(
                    conversation.conversation_id,
                    conversation.getConversationTitle(),
                    conversation.getUnwatchedCount(),
                    conversation.getImageUrl(authViewModel.getAuthentication())

            )
            //start widget update service
            val intent = Intent(this, WidgetService::class.java)
            intent.action = START_WIDGET_ACTION
            intent.putExtra(POLO_WIDGET_KEY, poloWidget)
            intent.putExtra(WIDGET_ID_KEY, appWidgetId)
            startService(intent)
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

