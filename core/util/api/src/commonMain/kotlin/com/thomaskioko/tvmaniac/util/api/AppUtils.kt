package com.thomaskioko.tvmaniac.util.api

import kotlinx.coroutines.flow.Flow

public interface AppUtils {

    public fun isYoutubePlayerInstalled(): Flow<Boolean>
}
