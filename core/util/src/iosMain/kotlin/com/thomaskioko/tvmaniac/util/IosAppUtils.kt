package com.thomaskioko.tvmaniac.util

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class IosAppUtils : AppUtils {

    override fun isYoutubePlayerInstalled(): Flow<Boolean> = flowOf(false)
}
