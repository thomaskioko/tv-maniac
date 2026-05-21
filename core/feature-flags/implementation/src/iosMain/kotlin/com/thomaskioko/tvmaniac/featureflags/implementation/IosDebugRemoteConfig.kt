package com.thomaskioko.tvmaniac.featureflags.implementation

import com.thomaskioko.tvmaniac.appconfig.DebugConfig
import com.thomaskioko.tvmaniac.featureflags.FeatureFlagLocalStore
import com.thomaskioko.tvmaniac.featureflags.FeatureFlagsRemoteConfig
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import com.thomaskioko.tvmaniac.featureflags.observe
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, replaces = [IosRemoteConfig::class])
public class IosDebugRemoteConfig(
    private val production: IosRemoteConfig,
    private val localStore: FeatureFlagLocalStore,
    private val debugConfig: DebugConfig,
) : FeatureFlagsRemoteConfig {

    override fun observeBoolean(key: String, default: Boolean): Flow<Boolean> =
        if (!debugConfig.isDebug) {
            production.observeBoolean(key, default)
        } else {
            combine(
                localStore.observe<Boolean>(key),
                production.observeBoolean(key, default),
            ) { local, remote -> local ?: remote }
                .distinctUntilChanged()
        }

    override fun observeSource(key: String): Flow<FeatureFlagSource> =
        if (!debugConfig.isDebug) {
            production.observeSource(key)
        } else {
            combine(
                localStore.observe<Boolean>(key),
                production.observeSource(key),
            ) { local, source -> if (local != null) FeatureFlagSource.Local else source }
                .distinctUntilChanged()
        }

    override suspend fun refresh() {
        production.refresh()
    }

    override suspend fun setDefaults(defaults: Map<String, Boolean>) {
        production.setDefaults(defaults)
    }
}
