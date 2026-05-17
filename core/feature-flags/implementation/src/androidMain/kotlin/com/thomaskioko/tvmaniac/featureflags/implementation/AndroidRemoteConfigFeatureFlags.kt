package com.thomaskioko.tvmaniac.featureflags.implementation

import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.featureflags.FeatureFlags
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.tasks.await

private const val LOG_TAG = "AndroidRemoteConfigFeatureFlags"

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class AndroidRemoteConfigFeatureFlags(
    private val remoteConfig: FirebaseRemoteConfig?,
    private val settings: FirebaseRemoteConfigSettings,
    private val state: FeatureFlagsState,
    private val logger: Logger,
    @IoCoroutineScope private val scope: CoroutineScope,
) : FeatureFlags {

    public suspend fun setup() {
        val config = remoteConfig
        if (config == null) {
            logger.debug(LOG_TAG, "FirebaseRemoteConfig unavailable, using enum defaults")
            return
        }
        logger.debug(LOG_TAG, "setup start (intervalSeconds=${settings.minimumFetchIntervalInSeconds})")
        config.setConfigSettingsAsync(settings).await()
        config.setDefaultsAsync(FeatureFlag.entries.associate { it.key to it.defaultValue }).await()
        registerListener(config)
    }

    override fun isEnabled(flag: FeatureFlag): Flow<Boolean> = state.isEnabled(flag)

    override fun source(flag: FeatureFlag): Flow<FeatureFlagSource> = state.source(flag)

    override suspend fun refresh() {
        val config = remoteConfig ?: return
        runCatching { config.fetchAndActivate().await() }
            .onSuccess { activated -> logger.debug(LOG_TAG, "fetchAndActivate succeeded (activated=$activated)") }
            .onFailure { logger.error(LOG_TAG, "fetchAndActivate failed", it) }
        updateFlagsFromRemoteConfig(config)
    }

    private fun updateFlagsFromRemoteConfig(config: FirebaseRemoteConfig) {
        val values = FeatureFlag.entries.associateWith { config.getBoolean(it.key) }
        state.update(values)
        logger.debug(LOG_TAG, "published values=$values")
    }

    private fun registerListener(config: FirebaseRemoteConfig) {
        configUpdates(config)
            .onEach { update ->
                logger.debug(LOG_TAG, "realtime update received (keys=${update.updatedKeys})")
                runCatching { config.activate().await() }
                    .onSuccess { updateFlagsFromRemoteConfig(config) }
                    .onFailure { logger.error(LOG_TAG, "Activation failed during realtime update", it) }
            }
            .launchIn(scope)
        logger.debug(LOG_TAG, "realtime listener registered")
    }

    private fun configUpdates(config: FirebaseRemoteConfig): Flow<ConfigUpdate> = callbackFlow {
        val registration = config.addOnConfigUpdateListener(
            object : ConfigUpdateListener {
                override fun onUpdate(configUpdate: ConfigUpdate) {
                    trySend(configUpdate)
                }

                override fun onError(error: FirebaseRemoteConfigException) {
                    logger.error(LOG_TAG, "Realtime config update error", error)
                }
            },
        )
        awaitClose { registration.remove() }
    }
}
