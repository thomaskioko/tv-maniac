package com.thomaskioko.tvmaniac.featureflags.testing

import com.thomaskioko.tvmaniac.featureflags.FeatureFlagsRemoteConfig
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

public class FakeFeatureFlagsRemoteConfig : FeatureFlagsRemoteConfig {

    private val valuesFlow: MutableStateFlow<Map<String, Boolean>> = MutableStateFlow(emptyMap())
    private val sourcesFlow: MutableStateFlow<Map<String, FeatureFlagSource>> = MutableStateFlow(emptyMap())

    public var refreshCount: Int = 0
        private set

    public var lastDefaults: Map<String, Boolean> = emptyMap()
        private set

    public fun setBoolean(key: String, value: Boolean) {
        valuesFlow.update { it + (key to value) }
    }

    public fun setSource(key: String, source: FeatureFlagSource) {
        sourcesFlow.update { it + (key to source) }
    }

    override fun observeBoolean(key: String, default: Boolean): Flow<Boolean> =
        valuesFlow
            .map { it[key] ?: default }
            .distinctUntilChanged()

    override fun observeSource(key: String): Flow<FeatureFlagSource> =
        sourcesFlow
            .map { it[key] ?: FeatureFlagSource.Firebase }
            .distinctUntilChanged()

    override suspend fun refresh() {
        refreshCount += 1
    }

    override suspend fun setDefaults(defaults: Map<String, Boolean>) {
        lastDefaults = defaults
        valuesFlow.update { defaults + it }
    }
}
