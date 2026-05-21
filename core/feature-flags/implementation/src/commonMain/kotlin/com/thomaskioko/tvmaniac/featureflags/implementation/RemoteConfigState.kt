package com.thomaskioko.tvmaniac.featureflags.implementation

import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Shared in-memory cache that the Firebase realtime listener writes into and that
 * [com.thomaskioko.tvmaniac.featureflags.FeatureFlagsRemoteConfig] reads from. Keyed by Remote
 * Config key (matching the underlying Firebase configuration). Source defaults to
 * [FeatureFlagSource.Firebase]; the `DebugRemoteConfig` decorator overlays
 * [FeatureFlagSource.Local] when an override is active.
 *
 * Constructed once at application scope; both `AndroidRemoteConfig` and `IosRemoteConfig` share
 * the same instance per platform graph.
 */
@SingleIn(AppScope::class)
@Inject
public class RemoteConfigState {

    private val valuesFlow: MutableStateFlow<Map<String, Boolean>> = MutableStateFlow(emptyMap())

    public fun observeBoolean(key: String, default: Boolean): Flow<Boolean> =
        valuesFlow
            .map { it[key] ?: default }
            .distinctUntilChanged()

    public fun observeSource(key: String): Flow<FeatureFlagSource> = flowOf(FeatureFlagSource.Firebase)

    public fun update(values: Map<String, Boolean>) {
        valuesFlow.update { it + values }
    }
}
