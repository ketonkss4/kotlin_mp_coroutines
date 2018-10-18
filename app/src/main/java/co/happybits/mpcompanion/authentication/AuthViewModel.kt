package co.happybits.mpcompanion.authentication

import android.content.Context
import android.widget.Toast
import co.happybits.mpcompanion.authentication.dependencies.LoginManager
import co.happybits.mpcompanion.authentication.dependencies.persistence.AuthPreferencesManager
import co.happybits.mpcompanion.concurrency.KtDispatchers
import kotlinx.coroutines.experimental.*

class AuthViewModel(
        private val loginManager: LoginManager,
        private val dispatchers: KtDispatchers,
        val authPrefs: AuthPreferencesManager
) {

    fun authenticateLogin(phoneNumber: String): Job {
        return GlobalScope.launch(dispatchers.ioDispatcher()) {
            runBlocking { loginManager.auth() }
            runBlocking { loginManager.login(phoneNumber, "US") }
            authPrefs.saveAuthPref(loginManager.apiToken, phoneNumber)
        }
    }

    suspend fun reAuthenticate() {
        coroutineScope {
            authPrefs.getPhoneAuthPref()?.let { authenticateLogin(it) }
        }
    }

    fun getAuthentication(): String {
        return authPrefs.getAuthPref()!!
    }

    fun onUserTriggeredAuthentication(context: Context, number: String) {
        if (isValidCellPhone(number)) {
            authenticateLogin(number).invokeOnCompletion { throwable ->
                GlobalScope.launch(dispatchers.uiDispatcher()) {
                    if (throwable != null) Toast.makeText(context, throwable.message, Toast.LENGTH_SHORT).show()
                    else Toast.makeText(context, "Authenticated", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Please Enter Valid MarcoPolo Phone Number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidCellPhone(number: String): Boolean {
        return android.util.Patterns.PHONE.matcher(number).matches()
    }

    fun onWidgetTriggeredAuthentication(context: Context, number: String, completeAuth: () -> Unit) {
        if (isValidCellPhone(number)) {
            authenticateLogin(number).invokeOnCompletion { throwable ->
                GlobalScope.launch(dispatchers.uiDispatcher()) {
                    if (throwable != null) {
                        Toast.makeText(context, throwable.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Authenticated", Toast.LENGTH_SHORT).show()
                        completeAuth()
                    }
                }
            }
        } else {
            Toast.makeText(context, "Please Enter Valid MarcoPolo Phone Number", Toast.LENGTH_SHORT).show()
        }
    }
}