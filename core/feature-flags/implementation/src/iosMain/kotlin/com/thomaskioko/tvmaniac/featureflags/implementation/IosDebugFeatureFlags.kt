package com.thomaskioko.tvmaniac.featureflags.implementation

import com.thomaskioko.tvmaniac.appconfig.DebugConfig
import com.thomaskioko.tvmaniac.featureflags.FeatureFlagLocalStore
import com.thomaskioko.tvmaniac.featureflags.FeatureFlags
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import com.thomaskioko.tvmaniac.featureflags.observe
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, replaces = [IosRemoteConfigFeatureFlags::class])
public class IosDebugFeatureFlags(
    private val production: IosRemoteConfigFeatureFlags,
    private val localStore: FeatureFlagLocalStore,
    private val debugConfig: DebugConfig,
) : FeatureFlags {

    override fun isEnabled(flag: FeatureFlag): Flow<Boolean> =
        if (!debugConfig.isDebug) {
            production.isEnabled(flag)
        } else {
            combine(
                localStore.observe<Boolean>(flag),
                production.isEnabled(flag),
            ) { local, remote -> local ?: remote }.distinctUntilChanged()
        }

    override fun source(flag: FeatureFlag): Flow<FeatureFlagSource> =
        if (!debugConfig.isDebug) {
            production.source(flag)
        } else {
            combine(
                localStore.observe<Boolean>(flag),
                production.source(flag),
            ) { local, prodSource ->
                if (local != null) FeatureFlagSource.Local else prodSource
            }.distinctUntilChanged()
        }

    override suspend fun refresh() {
        production.refresh()
    }
}
