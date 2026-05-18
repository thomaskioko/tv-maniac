package com.thomaskioko.tvmaniac.featureflags

import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag

public interface FeatureFlagEditor<Value : Any> {
    public val flag: FeatureFlag
    public suspend fun set(value: Value?)
}
