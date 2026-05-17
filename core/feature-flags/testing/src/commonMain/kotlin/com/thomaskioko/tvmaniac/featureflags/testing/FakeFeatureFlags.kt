package com.thomaskioko.tvmaniac.featureflags.testing

import com.thomaskioko.tvmaniac.featureflags.FeatureFlags
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

public class FakeFeatureFlags : FeatureFlags {

    private val valuesFlow: MutableStateFlow<Map<FeatureFlag, Boolean>> =
        MutableStateFlow(FeatureFlag.entries.associateWith { it.defaultValue })

    public fun setEnabled(flag: FeatureFlag, value: Boolean) {
        valuesFlow.value = valuesFlow.value + (flag to value)
    }

    override fun isEnabled(flag: FeatureFlag): Flow<Boolean> =
        valuesFlow
            .map { it[flag] ?: flag.defaultValue }
            .distinctUntilChanged()

    override fun source(flag: FeatureFlag): Flow<FeatureFlagSource> = flowOf(FeatureFlagSource.Firebase)

    override suspend fun refresh() {
        // No-op: the fake exposes deterministic values via setEnabled; no network fetch to perform.
    }
}
