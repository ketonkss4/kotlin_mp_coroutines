package co.happybits.mpcompanion.authentication

import co.happybits.mpcompanion.authentication.dependencies.LoginManager
import co.happybits.mpcompanion.authentication.dependencies.awaitAuthResult
import co.happybits.mpcompanion.authentication.dependencies.persistence.AuthPreferencesManager
import co.happybits.mpcompanion.concurrency.CoroutineScopedViewModel
import co.happybits.mpcompanion.concurrency.KtDispatchers
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.launch

class AuthViewModel(
        private val loginManager: LoginManager,
        override val dispatchers: KtDispatchers,
        val authPrefs: AuthPreferencesManager
) : CoroutineScopedViewModel() {

    private fun authenticateLogin(
            phoneNumber: String,
            sendChannel: SendChannel<Result>
    ) {
        launch(dispatchers.ioDispatcher()) {

            val authResult = loginManager.auth().awaitAuthResult()
            if (authResult.isSuccessful && authResult.result != null) {

                val result = loginManager.login(phoneNumber, "US", authResult.result).awaitAuthResult()
                if (result.isSuccessful) {
                    result.result?.let {
                        authPrefs.saveAuthPref(it, phoneNumber)
                        sendChannel.send(Result(true, null))
                    }
                } else {
                    sendChannel.send(Result(false, result.exception?.message))
                }
            } else {
                sendChannel.send(Result(false, authResult.exception?.message))
            }
        }
    }


    suspend fun reAuthenticate() {
        authPrefs.getAuthPref()?.let { authenticate(it).await() }
    }

    fun getAuthentication(): String {
        return authPrefs.getAuthPref()!!
    }

    private fun isValidCellPhone(number: String): Boolean {
        return android.util.Patterns.PHONE.matcher(number).matches()
    }

    fun authenticate(number: String): Deferred<Result> {
        return async {
            val channel = Channel<Result>()
            if (isValidCellPhone(number)) {
                authenticateLogin(number, channel)
                val result = channel.receive()
                result
            } else {
                Result(false, "Please Enter Valid MarcoPolo Phone Number")
            }
        }
    }
}

data class Result(val isSuccessful: Boolean, val failMsg: String?)