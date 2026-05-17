package com.thomaskioko.tvmaniac.featureflags.implementation

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.featureflags.FeatureFlags
import com.thomaskioko.tvmaniac.featureflags.RemoteConfigBridge
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagFetchInterval
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

private const val LOG_TAG = "IosRemoteConfigFeatureFlags"

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class IosRemoteConfigFeatureFlags(
    private val bridge: RemoteConfigBridge,
    private val fetchInterval: FeatureFlagFetchInterval,
    private val state: FeatureFlagsState,
    private val logger: Logger,
) : FeatureFlags {

    public suspend fun setup() {
        logger.debug(LOG_TAG, "setup start (bridge=${bridge::class.simpleName}, intervalSeconds=${fetchInterval.seconds})")
        bridge.setMinimumFetchIntervalSeconds(fetchInterval.seconds)
        bridge.setDefaults(FeatureFlag.entries.associate { it.key to it.defaultValue })
        bridge.addOnConfigUpdateListener {
            logger.debug(LOG_TAG, "realtime update received")
            runCatching { publishCurrent() }
                .onFailure { logger.error(LOG_TAG, "realtime update failed", it) }
        }
        logger.debug(LOG_TAG, "realtime listener registered")
    }

    override fun isEnabled(flag: FeatureFlag): Flow<Boolean> = state.isEnabled(flag)

    override fun source(flag: FeatureFlag): Flow<FeatureFlagSource> = state.source(flag)

    override suspend fun refresh() {
        logger.debug(LOG_TAG, "refresh start")
        runCatching {
            suspendCancellableCoroutine { continuation ->
                bridge.fetchAndActivate { result -> continuation.resume(result) }
            }
        }
            .onSuccess { activated -> logger.debug(LOG_TAG, "fetchAndActivate succeeded (activated=$activated)") }
            .onFailure { logger.error(LOG_TAG, "fetchAndActivate failed", it) }
        publishCurrent()
    }

    private fun publishCurrent() {
        val values = FeatureFlag.entries.associateWith { bridge.getBoolean(it.key) }
        state.update(values)
        logger.debug(LOG_TAG, "published values=$values")
    }
}
