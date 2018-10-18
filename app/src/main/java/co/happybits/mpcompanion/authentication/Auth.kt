package co.happybits.mpcompanion.authentication

import android.content.Context
import android.widget.Toast
import co.happybits.mpcompanion.authentication.dependencies.LoginManager
import co.happybits.mpcompanion.authentication.dependencies.persistence.AuthPreferencesManager
import co.happybits.mpcompanion.concurrency.KtDispatchers
import co.happybits.mpcompanion.concurrency.CoroutineScopedViewModel
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.coroutineScope
import kotlinx.coroutines.experimental.launch
import okhttp3.Response

class Auth(
        private val loginManager: LoginManager,
        override val dispatchers: KtDispatchers,
        val authPrefs: AuthPreferencesManager
) : CoroutineScopedViewModel() {

    private suspend fun authenticateLogin(
            phoneNumber: String,
            context: Context,
            completeAuth: (() -> Unit),
            onAuthError: (() -> Unit)
    ) {
        launch(dispatchers.ioDispatcher()) {
            val authRequest = loginManager.auth()
            handleAuthCompletion(authRequest, context, onAuthError)
            authRequest.await()
            if (!authRequest.isCancelled) {
                val loginRequest = loginManager.login(phoneNumber, "US")
                handleLoginCompletion(loginRequest, context, phoneNumber, onAuthError)
                loginRequest.await()
                if (!loginRequest.isCancelled) completeAuth()
            }
        }
    }

    private suspend fun authenticateLogin(phoneNumber: String) {
        val authRequest = loginManager.auth()
        authRequest.await()
        if (!authRequest.isCancelled) {
            val loginRequest = loginManager.login(phoneNumber, "US")
            loginRequest.await()
            if (!loginRequest.isCancelled) loginManager.apiToken?.let { token ->
                authPrefs.saveAuthPref(token, phoneNumber)
            }
        }
    }

    private fun handleAuthCompletion(authRequest: Deferred<Response>, context: Context, onAuthErrorCallback: (() -> Unit)) {
        authRequest.invokeOnCompletion {
            if (it != null) {
                onAuthRequestError(it.message, context)
                onAuthErrorCallback()
            }
        }
    }

    private fun handleLoginCompletion(loginRequest: Deferred<Boolean>, context: Context, phoneNumber: String, onAuthErrorCallback: (() -> Unit)) {
        loginRequest.invokeOnCompletion {
            if (it != null) {
                onAuthRequestError(it.message, context)
                onAuthErrorCallback()
            } else {
                loginManager.apiToken?.let { token -> authPrefs.saveAuthPref(token, phoneNumber) }
                onAuthRequestComplete(context)
            }
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

    fun onUserTriggeredAuthentication(context: Context, number: String, onAuthComplete: () -> Unit): Job {
        return launch {
            if (isValidCellPhone(number)) {
                authenticateLogin(number, context, onAuthComplete, onAuthComplete)
            } else {
                Toast.makeText(context, "Please Enter Valid MarcoPolo Phone Number", Toast.LENGTH_SHORT).show()
                onAuthComplete()
            }
        }
    }

    private fun onAuthRequestError(errorMsg: String?, context: Context) {
        launch {
            if (errorMsg != null) Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onAuthRequestComplete(context: Context) {
        launch {
            Toast.makeText(context, "Authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidCellPhone(number: String): Boolean {
        return android.util.Patterns.PHONE.matcher(number).matches()
    }

    fun onWidgetTriggeredAuthentication(context: Context, number: String, completeAuth: () -> Unit, errorCallback: () -> Unit) {
        launch {
            if (isValidCellPhone(number)) {
                authenticateLogin(number, context, completeAuth, errorCallback)
            } else {
                Toast.makeText(context, "Please Enter Valid MarcoPolo Phone Number", Toast.LENGTH_SHORT).show()
                errorCallback()
            }
        }
    }
}