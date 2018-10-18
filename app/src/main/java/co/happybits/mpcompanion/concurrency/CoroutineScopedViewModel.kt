package co.happybits.mpcompanion.concurrency

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Job
import kotlin.coroutines.experimental.CoroutineContext

abstract class CoroutineScopedViewModel : ViewModel() , CoroutineScope {
    abstract val dispatchers: KtDispatchers
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + dispatchers.uiDispatcher()

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}