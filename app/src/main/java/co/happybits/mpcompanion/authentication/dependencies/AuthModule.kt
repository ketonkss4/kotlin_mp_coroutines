package co.happybits.mpcompanion.authentication.dependencies

import co.happybits.mpcompanion.MpCompanion
import co.happybits.mpcompanion.authentication.AuthViewModel
import co.happybits.mpcompanion.concurrency.KtDispatchers
import dagger.Module
import dagger.Provides

@Module
class AuthModule {

    @Provides fun provideAuthActivityViewModel(
            dispatchers: KtDispatchers,
            loginManager: LoginManager
    ): AuthViewModel {
        return AuthViewModel(MpCompanion.instance,
                loginManager,
                dispatchers
        )
    }

    @Provides fun provideLoginManager() : LoginManager {
        return LoginManager.getInstance()
    }
}