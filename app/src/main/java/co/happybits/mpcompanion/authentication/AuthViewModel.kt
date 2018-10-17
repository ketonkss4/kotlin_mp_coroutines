package co.happybits.mpcompanion.authentication

import co.happybits.mpcompanion.authentication.dependencies.LoginManager
import co.happybits.mpcompanion.authentication.dependencies.TokenProvider
import co.happybits.mpcompanion.concurrency.KtDispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch

class AuthViewModel(
        private val tokenProvider: TokenProvider,
        private val loginManager: LoginManager,
        private val dispatchers: KtDispatchers
) {

    fun authenticateLogin(): Job {
        return GlobalScope.launch(dispatchers.ioDispatcher()) {
            if(!loginManager.isRegistered) {
                loginManager.auth()
                loginManager.login("6125018293", "US")
                tokenProvider.setApiToken(loginManager.apiToken)
            }
        }
    }
}