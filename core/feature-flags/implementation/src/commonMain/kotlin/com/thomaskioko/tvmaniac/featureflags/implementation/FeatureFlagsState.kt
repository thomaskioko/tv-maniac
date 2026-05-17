package com.thomaskioko.tvmaniac.featureflags.implementation

import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
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

@SingleIn(AppScope::class)
@Inject
public class FeatureFlagsState {

    private val valuesFlow: MutableStateFlow<Map<FeatureFlag, Boolean>> =
        MutableStateFlow(FeatureFlag.entries.associateWith { it.defaultValue })

    public fun isEnabled(flag: FeatureFlag): Flow<Boolean> =
        valuesFlow
            .map { it[flag] ?: flag.defaultValue }
            .distinctUntilChanged()

    public fun source(flag: FeatureFlag): Flow<FeatureFlagSource> = flowOf(FeatureFlagSource.Firebase)

    public fun update(values: Map<FeatureFlag, Boolean>) {
        valuesFlow.update { it + values }
    }
}
