package com.thomaskioko.tvmaniac.shared.core.ui

import kotlinx.coroutines.CoroutineScope

expect class CoroutineScopeProvider {

    val coroutineScope: CoroutineScope
}