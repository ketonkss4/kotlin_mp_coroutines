package co.happybits.mpcompanion.authentication

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import butterknife.BindString
import butterknife.BindView
import butterknife.ButterKnife
import co.happybits.mpcompanion.R
import co.happybits.mpcompanion.authentication.dependencies.DaggerAuthComponent
import co.happybits.mpcompanion.authentication.dependencies.persistence.hasSavedAuth
import co.happybits.mpcompanion.concurrency.CoroutineScopedActivity
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class AuthActivity : CoroutineScopedActivity() {

    @Inject
    lateinit var authViewModel: Auth
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

        authenticateButton.setOnClickListener { button ->
            val number = phoneNumberInput.text.toString()
            button.visibility = GONE
            progressIndicator.visibility = VISIBLE
            authViewModel.onUserTriggeredAuthentication(this, number) {
                onAuthorizationComplete()
            }
        }
    }

    private fun onAuthorizationComplete() {
        launch {
            authenticateButton.visibility = VISIBLE
            progressIndicator.visibility = GONE
            authenticateButton.text = reauthText
        }
    }

    override fun onResume() {
        super.onResume()
        if (authViewModel.authPrefs.hasSavedAuth()) {
            phoneNumberInput.setText(authViewModel.authPrefs.getPhoneAuthPref())
            authenticateButton.text = reauthText
        }
    }
}
