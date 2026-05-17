package com.thomaskioko.tvmaniac.featureflags.implementation

import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.thomaskioko.tvmaniac.core.base.AsyncInitializers
import com.thomaskioko.tvmaniac.core.base.Initializer
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagFetchInterval
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val LOG_TAG = "FeatureFlagInitializer"

@BindingContainer
@ContributesTo(AppScope::class)
public object AndroidFeatureFlagsBindingContainer {

    @Provides
    @IntoSet
    @AsyncInitializers
    public fun provideRemoteConfigInitializer(
        bind: AndroidRemoteConfigFeatureFlags,
        @IoCoroutineScope scope: CoroutineScope,
        logger: Logger,
    ): Initializer = Initializer {
        scope.launch {
            runCatching {
                bind.setup()
                bind.refresh()
            }.onFailure { logger.error(LOG_TAG, "feature flag init failed", it) }
        }
    }

    @Provides
    @SingleIn(AppScope::class)
    public fun provideFirebaseRemoteConfig(
        firebaseApp: FirebaseApp?,
    ): FirebaseRemoteConfig? = firebaseApp?.let { FirebaseRemoteConfig.getInstance() }

    @Provides
    @SingleIn(AppScope::class)
    public fun provideFirebaseRemoteConfigSettings(
        interval: FeatureFlagFetchInterval,
    ): FirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings.Builder()
        .setMinimumFetchIntervalInSeconds(interval.seconds)
        .build()
}
