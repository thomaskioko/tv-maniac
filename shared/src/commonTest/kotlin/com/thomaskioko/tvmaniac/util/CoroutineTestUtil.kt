package com.thomaskioko.tvmaniac.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

expect fun runBlockingTest(block: suspend CoroutineScope.() -> Unit)
expect val testCoroutineContext: CoroutineContext
expect val testCoroutineScope: CoroutineScope
expect val testCoroutineDispatcher: CoroutineDispatcher
