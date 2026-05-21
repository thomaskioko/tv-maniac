package com.thomaskioko.tvmaniac.featureflags.implementation

import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.featureflags.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.FeatureFlagsRemoteConfig
import com.thomaskioko.tvmaniac.featureflags.RemoteFlag
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

private const val LOG_TAG = "AndroidRemoteConfig"

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class AndroidRemoteConfig(
    private val remoteConfig: FirebaseRemoteConfig?,
    private val settings: FirebaseRemoteConfigSettings,
    private val state: RemoteConfigState,
    private val flags: Lazy<Set<FeatureFlag<Boolean>>>,
    private val logger: Logger,
    @IoCoroutineScope private val scope: CoroutineScope,
) : FeatureFlagsRemoteConfig {

    public suspend fun setup() {
        val config = remoteConfig
        if (config == null) {
            logger.debug(LOG_TAG, "FirebaseRemoteConfig unavailable, using flag defaults")
            return
        }
        logger.debug(LOG_TAG, "setup start (intervalSeconds=${settings.minimumFetchIntervalInSeconds})")
        config.setConfigSettingsAsync(settings).await()
        setDefaults(flagDefaults())
        registerListener(config)
    }

    override fun observeBoolean(key: String, default: Boolean): Flow<Boolean> =
        state.observeBoolean(key, default)

    override fun observeSource(key: String): Flow<FeatureFlagSource> = state.observeSource(key)

    override suspend fun refresh() {
        val config = remoteConfig ?: run {
            logger.debug(LOG_TAG, "refresh skipped (FirebaseRemoteConfig unavailable)")
            return
        }
        runCatching { config.fetchAndActivate().await() }
            .onSuccess { activated -> logger.debug(LOG_TAG, "fetchAndActivate succeeded (activated=$activated)") }
            .onFailure { logger.error(LOG_TAG, "fetchAndActivate failed", it) }
        updateFromRemoteConfig(config)
    }

    override suspend fun setDefaults(defaults: Map<String, Boolean>) {
        val config = remoteConfig ?: run {
            state.update(defaults)
            return
        }
        config.setDefaultsAsync(defaults).await()
        state.update(defaults)
    }

    private fun flagDefaults(): Map<String, Boolean> =
        flags.value.filterIsInstance<RemoteFlag>().associate { it.key to it.defaultValue }

    private fun updateFromRemoteConfig(config: FirebaseRemoteConfig) {
        val values = flags.value.filterIsInstance<RemoteFlag>().associate { it.key to config.getBoolean(it.key) }
        state.update(values)
        logger.debug(LOG_TAG, "published values=$values")
    }

    private fun registerListener(config: FirebaseRemoteConfig) {
        configUpdates(config)
            .onEach { update ->
                logger.debug(LOG_TAG, "realtime update received (keys=${update.updatedKeys})")
                runCatching { config.activate().await() }
                    .onSuccess { updateFromRemoteConfig(config) }
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
