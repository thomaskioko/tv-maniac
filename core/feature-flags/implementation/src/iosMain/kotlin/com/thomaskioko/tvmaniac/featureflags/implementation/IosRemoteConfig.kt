package com.thomaskioko.tvmaniac.featureflags.implementation

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.featureflags.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.FeatureFlagsRemoteConfig
import com.thomaskioko.tvmaniac.featureflags.RemoteConfigBridge
import com.thomaskioko.tvmaniac.featureflags.RemoteFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagFetchInterval
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

private const val LOG_TAG = "IosRemoteConfig"

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class IosRemoteConfig(
    private val bridge: RemoteConfigBridge,
    private val fetchInterval: FeatureFlagFetchInterval,
    private val state: RemoteConfigState,
    private val flags: Lazy<Set<FeatureFlag<Boolean>>>,
    private val logger: Logger,
) : FeatureFlagsRemoteConfig {

    public suspend fun setup() {
        logger.debug(LOG_TAG, "setup start (bridge=${bridge::class.simpleName}, intervalSeconds=${fetchInterval.seconds})")
        bridge.setMinimumFetchIntervalSeconds(fetchInterval.seconds)
        setDefaults(flagDefaults())
        bridge.addOnConfigUpdateListener {
            logger.debug(LOG_TAG, "realtime update received")
            runCatching { publishCurrent() }
                .onFailure { logger.error(LOG_TAG, "realtime update failed", it) }
        }
        logger.debug(LOG_TAG, "realtime listener registered")
    }

    override fun observeBoolean(key: String, default: Boolean): Flow<Boolean> =
        state.observeBoolean(key, default)

    override fun observeSource(key: String): Flow<FeatureFlagSource> = state.observeSource(key)

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

    override suspend fun setDefaults(defaults: Map<String, Boolean>) {
        bridge.setDefaults(defaults)
        state.update(defaults)
    }

    private fun flagDefaults(): Map<String, Boolean> =
        flags.value.filterIsInstance<RemoteFlag>().associate { it.key to it.defaultValue }

    private fun publishCurrent() {
        val values = flags.value.filterIsInstance<RemoteFlag>().associate { it.key to bridge.getBoolean(it.key) }
        state.update(values)
        logger.debug(LOG_TAG, "published values=$values")
    }
}
