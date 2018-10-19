package co.happybits.mpcompanion.authentication.dependencies

import co.happybits.mpcompanion.MpCompanion
import co.happybits.mpcompanion.authentication.AuthViewModel
import co.happybits.mpcompanion.authentication.dependencies.persistence.AuthPreferences
import co.happybits.mpcompanion.authentication.dependencies.persistence.AuthPreferencesManager
import co.happybits.mpcompanion.concurrency.KtDispatchers
import dagger.Module
import dagger.Provides

@Module
class AuthModule {

    @Provides
    fun provideAuthActivityViewModel(
            dispatchers: KtDispatchers,
            loginManager: LoginManager,
            authPreferencesManager: AuthPreferencesManager
    ): AuthViewModel {
        return AuthViewModel(
                loginManager,
                dispatchers,
                authPreferencesManager
        )
    }

    @Provides
    fun provideLoginManager(): LoginManager {
        return LoginManager.instance
    }

    @Provides
    fun provideAuthPreferencesManager(): AuthPreferencesManager = AuthPreferences(MpCompanion.instance)

}