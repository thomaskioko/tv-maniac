package com.thomaskioko.tvmaniac.core.util.scope

import kotlinx.coroutines.CoroutineScope

expect class CoroutineScopeProvider() {

    val coroutineScope: CoroutineScope
}