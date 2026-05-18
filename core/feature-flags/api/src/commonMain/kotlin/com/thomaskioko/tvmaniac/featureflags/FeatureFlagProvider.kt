package com.thomaskioko.tvmaniac.featureflags

import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSortDescriptor

public interface FeatureFlagProvider {
    public fun flags(sort: FeatureFlagSortDescriptor, ascending: Boolean): List<FeatureFlag>

    public fun findFeatureFlag(key: String): FeatureFlag?

    public suspend fun resetAllLocals()
}
