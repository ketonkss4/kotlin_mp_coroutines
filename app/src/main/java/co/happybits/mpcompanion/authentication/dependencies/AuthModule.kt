package co.happybits.mpcompanion.authentication.dependencies

import co.happybits.mpcompanion.MpCompanion
import co.happybits.mpcompanion.authentication.AuthActivityViewModel
import co.happybits.mpcompanion.concurrency.KtDispatchers
import dagger.Module
import dagger.Provides

@Module
class AuthModule {

    @Provides fun provideAuthActivityViewModel(dispatchers: KtDispatchers): AuthActivityViewModel {
        return AuthActivityViewModel(MpCompanion.instance,
                LoginManager.getInstance(),
                dispatchers
        )
    }
}