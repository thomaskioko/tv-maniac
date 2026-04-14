package com.thomaskioko.tvmaniac.util

import com.thomaskioko.tvmaniac.util.api.AppUtils
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class IosAppUtils : AppUtils {

    override fun isYoutubePlayerInstalled(): Flow<Boolean> = flowOf(false)
}
