package com.thomaskioko.tvmaniac.core.test

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

actual val testCoroutineDispatcher: CoroutineDispatcher =
    Executors.newSingleThreadExecutor().asCoroutineDispatcher()

actual val testCoroutineContext: CoroutineContext =
    Executors.newSingleThreadExecutor().asCoroutineDispatcher()

actual val testCoroutineScope: CoroutineScope = CoroutineScope(testCoroutineContext)

actual fun runBlockingTest(block: suspend CoroutineScope.() -> Unit) =
    runBlocking(testCoroutineContext) { this.block() }
