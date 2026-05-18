package com.thomaskioko.tvmaniac.featureflags.testing

import com.thomaskioko.tvmaniac.featureflags.FeatureFlags
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

public class FakeFeatureFlags : FeatureFlags {

    private val valuesFlow: MutableStateFlow<Map<FeatureFlag, Boolean>> =
        MutableStateFlow(FeatureFlag.entries.associateWith { it.defaultValue })

    private val sourcesFlow: MutableStateFlow<Map<FeatureFlag, FeatureFlagSource>> =
        MutableStateFlow(FeatureFlag.entries.associateWith { FeatureFlagSource.Firebase })

    public var refreshCount: Int = 0
        private set

    public fun setEnabled(flag: FeatureFlag, value: Boolean) {
        valuesFlow.update { it + (flag to value) }
    }

    public fun setSource(flag: FeatureFlag, source: FeatureFlagSource) {
        sourcesFlow.update { it + (flag to source) }
    }

    override fun isEnabled(flag: FeatureFlag): Flow<Boolean> =
        valuesFlow
            .map { it[flag] ?: flag.defaultValue }
            .distinctUntilChanged()

    override fun source(flag: FeatureFlag): Flow<FeatureFlagSource> =
        sourcesFlow
            .map { it[flag] ?: FeatureFlagSource.Firebase }
            .distinctUntilChanged()

    override suspend fun refresh() {
        refreshCount += 1
    }
}
