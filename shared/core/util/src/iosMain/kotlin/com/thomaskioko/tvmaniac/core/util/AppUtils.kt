package com.thomaskioko.tvmaniac.core.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

actual class AppUtils actual constructor(context: AppContext) {
    actual fun isYoutubePlayerInstalled(): Flow<Boolean> = flowOf(false)
}