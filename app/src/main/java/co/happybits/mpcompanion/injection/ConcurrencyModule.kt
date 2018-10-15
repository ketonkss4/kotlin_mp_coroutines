package co.happybits.mpcompanion.injection

import co.happybits.mpcompanion.concurrency.AppDispatchers
import co.happybits.mpcompanion.concurrency.KtDispatchers
import dagger.Module
import dagger.Provides

@Module class   ConcurrencyModule {
    @Provides fun provideCoroutineDispatchers(): KtDispatchers {
        return AppDispatchers()
    }
}