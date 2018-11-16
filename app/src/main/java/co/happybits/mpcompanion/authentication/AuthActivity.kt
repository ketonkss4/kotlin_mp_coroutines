package co.happybits.mpcompanion.authentication

import android.annotation.TargetApi
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import butterknife.BindString
import butterknife.BindView
import butterknife.ButterKnife
import co.happybits.mpcompanion.R
import co.happybits.mpcompanion.authentication.dependencies.DaggerAuthComponent
import co.happybits.mpcompanion.authentication.dependencies.persistence.hasSavedAuth
import co.happybits.mpcompanion.concurrency.CoroutineScopedActivity
import co.happybits.mpcompanion.widget.WidgetViewProvider
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class AuthActivity : CoroutineScopedActivity() {

    @Inject
    lateinit var authViewModel: AuthViewModel
    @BindView(R.id.editText)
    lateinit var phoneNumberInput: EditText
    @BindView(R.id.button)
    lateinit var authenticateButton: Button
    @BindView(R.id.auth_progress_bar)
    lateinit var progressIndicator: ProgressBar
    @BindString(R.string.reauthenticate_btn_txt)
    lateinit var reauthText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        DaggerAuthComponent.builder().build().inject(this)
        phoneNumberInput.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        authenticateButton.setOnClickListener(onAuthButtonClick())
    }

    private fun onAuthButtonClick(): View.OnClickListener {
        return View.OnClickListener { button ->
            val number = phoneNumberInput.text.toString()
            button.visibility = GONE
            progressIndicator.visibility = VISIBLE
            requestAuth(number)
        }
    }

    private fun requestAuth(number: String) {
        launch {
            val result = authViewModel.authenticate(number).await()
            if (result.isSuccessful) {
                onAuthorizationComplete()
                attemptWidgetPinning()
            } else {
                onAuthFailed(result.failMsg)
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun attemptWidgetPinning() {
        val mAppWidgetManager = getSystemService(AppWidgetManager::class.java)
        val myProvider = ComponentName(this, WidgetViewProvider::class.java)
        mAppWidgetManager.requestPinAppWidget(myProvider, null, null);
    }

    private fun onAuthFailed(failMsg: String?) {
        showAuthBtn()
        failMsg?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
    }

    private fun onAuthorizationComplete() {
        showAuthBtn()
        if (authViewModel.authPrefs.hasSavedAuth()) {
            authenticateButton.text = reauthText
            Toast.makeText(this@AuthActivity, "Authorized", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAuthBtn() {
        authenticateButton.visibility = VISIBLE
        progressIndicator.visibility = GONE
    }

    override fun onResume() {
        super.onResume()
        if (authViewModel.authPrefs.hasSavedAuth()) {
            phoneNumberInput.setText(authViewModel.authPrefs.getPhoneAuthPref())
            authenticateButton.text = reauthText
        }
    }
}
