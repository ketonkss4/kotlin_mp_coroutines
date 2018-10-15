package co.happybits.mpcompanion.concurrency

import kotlinx.coroutines.experimental.CoroutineDispatcher

interface KtDispatchers {
    fun ioDispatcher(): CoroutineDispatcher
    fun uiDispatcher() : CoroutineDispatcher
    fun testDispatcher() : CoroutineDispatcher
}