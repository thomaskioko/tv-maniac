package com.thomaskioko.tvmaniac.domain.featureflags

import com.thomaskioko.tvmaniac.featureflags.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource

public data class FeatureFlagRow(
    val featureFlag: FeatureFlag<Boolean>,
    val value: Boolean,
    val featureFlagSource: FeatureFlagSource,
)
