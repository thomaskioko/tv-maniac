package com.thomaskioko.tvmaniac.featureflags

import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import kotlinx.coroutines.flow.Flow

public interface FeatureFlags {
    public fun isEnabled(flag: FeatureFlag): Flow<Boolean>

    public fun source(flag: FeatureFlag): Flow<FeatureFlagSource>

    public suspend fun refresh()
}
