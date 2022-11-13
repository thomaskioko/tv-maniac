package com.thomaskioko.tvmaniac.shared.core.ui

import kotlinx.coroutines.CoroutineScope

actual class CoroutineScopeProvider {

    actual  val coroutineScope: CoroutineScope = NsQueueCoroutineScope()
}