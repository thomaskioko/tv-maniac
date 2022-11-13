package com.thomaskioko.tvmaniac.shared.core.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

actual class CoroutineScopeProvider {

    actual val coroutineScope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
}