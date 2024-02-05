package com.thomaskioko.tvmaniac.util

import kotlinx.coroutines.flow.Flow

interface AppUtils {

  fun isYoutubePlayerInstalled(): Flow<Boolean>
}
