package co.happybits.mpcompanion.authentication

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import co.happybits.mpcompanion.R
import co.happybits.mpcompanion.authentication.dependencies.DaggerAuthComponent
import co.happybits.mpcompanion.authentication.dependencies.persistence.hasSavedAuth
import javax.inject.Inject

class AuthActivity : AppCompatActivity() {

    @Inject
    lateinit var authViewModel: AuthViewModel
    @BindView(R.id.editText)
    lateinit var phoneNumberInput: EditText
    @BindView(R.id.button)
    lateinit var authenticateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        DaggerAuthComponent.builder().build().inject(this)
        phoneNumberInput.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        if (authViewModel.authPrefs.hasSavedAuth()) {
            phoneNumberInput.setText(authViewModel.authPrefs.getPhoneAuthPref())
            authenticateButton.text = "Re-Autheticate"
        }
        authenticateButton.setOnClickListener {
            val number = phoneNumberInput.text.toString()
            authViewModel.onUserTriggeredAuthentication(this, number)
        }

    }
}
