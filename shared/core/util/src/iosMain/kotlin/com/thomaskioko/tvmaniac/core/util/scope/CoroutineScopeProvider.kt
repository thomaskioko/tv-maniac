package com.thomaskioko.tvmaniac.core.util.scope

import kotlinx.coroutines.CoroutineScope

actual class CoroutineScopeProvider {

    actual  val coroutineScope: CoroutineScope = NsQueueCoroutineScope()
}