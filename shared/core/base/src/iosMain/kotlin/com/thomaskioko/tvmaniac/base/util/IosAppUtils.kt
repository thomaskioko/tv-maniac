package com.thomaskioko.tvmaniac.base.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.tatarka.inject.annotations.Inject

@Inject
class IosAppUtils : AppUtils {

    override fun isYoutubePlayerInstalled(): Flow<Boolean> = flowOf(false)
}