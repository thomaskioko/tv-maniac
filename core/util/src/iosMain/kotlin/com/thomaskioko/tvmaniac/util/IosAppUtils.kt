package com.thomaskioko.tvmaniac.util

import com.thomaskioko.tvmaniac.util.api.AppUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class IosAppUtils : AppUtils {

    override fun isYoutubePlayerInstalled(): Flow<Boolean> = flowOf(false)
}
