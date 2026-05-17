package com.thomaskioko.tvmaniac.featureflags.implementation

import com.thomaskioko.tvmaniac.core.base.AsyncInitializers
import com.thomaskioko.tvmaniac.core.base.Initializer
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val LOG_TAG = "FeatureFlagInitializer"

@BindingContainer
@ContributesTo(AppScope::class)
public object IosFeatureFlagsBindingContainer {

    @Provides
    @IntoSet
    @AsyncInitializers
    public fun provideRemoteConfigInitializer(
        bind: IosRemoteConfigFeatureFlags,
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
}
