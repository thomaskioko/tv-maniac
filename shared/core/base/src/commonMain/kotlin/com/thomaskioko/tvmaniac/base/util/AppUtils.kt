package com.thomaskioko.tvmaniac.base.util

import kotlinx.coroutines.flow.Flow

interface AppUtils {

    fun isYoutubePlayerInstalled(): Flow<Boolean>
}