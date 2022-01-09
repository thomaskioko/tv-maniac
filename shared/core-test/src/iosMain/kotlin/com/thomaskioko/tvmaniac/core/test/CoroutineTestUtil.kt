package com.thomaskioko.tvmaniac.core.test

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

actual val testCoroutineDispatcher: CoroutineDispatcher =
    newSingleThreadContext("testRunner")

actual val testCoroutineContext: CoroutineContext =
    newSingleThreadContext("testRunner")

actual val testCoroutineScope: CoroutineScope = CoroutineScope(testCoroutineContext)

actual fun runBlockingTest(block: suspend CoroutineScope.() -> Unit) =
    runBlocking(testCoroutineContext) { this.block() }
