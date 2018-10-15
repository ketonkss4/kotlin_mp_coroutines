package co.happybits.mpcompanion.concurrency

import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.android.Main

class AppDispatchers : KtDispatchers {

    override fun ioDispatcher(): CoroutineDispatcher {
        return Dispatchers.Default
    }

    override fun uiDispatcher() : CoroutineDispatcher {
        return Dispatchers.Main
    }

    override fun testDispatcher() : CoroutineDispatcher {
        return Dispatchers.Unconfined
    }
}
