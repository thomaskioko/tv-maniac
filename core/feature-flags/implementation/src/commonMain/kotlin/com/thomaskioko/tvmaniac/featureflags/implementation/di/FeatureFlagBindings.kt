package com.thomaskioko.tvmaniac.featureflags.implementation.di

import com.thomaskioko.tvmaniac.appconfig.DebugConfig
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagFetchInterval
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

private const val DEBUG_FETCH_INTERVAL_SECONDS = 900L
private const val RELEASE_FETCH_INTERVAL_SECONDS = 43_200L

@BindingContainer
@ContributesTo(AppScope::class)
public object FeatureFlagBindings {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideFetchInterval(
        debugConfig: DebugConfig,
    ): FeatureFlagFetchInterval = FeatureFlagFetchInterval(
        seconds = if (debugConfig.isDebug) DEBUG_FETCH_INTERVAL_SECONDS else RELEASE_FETCH_INTERVAL_SECONDS,
    )
}
