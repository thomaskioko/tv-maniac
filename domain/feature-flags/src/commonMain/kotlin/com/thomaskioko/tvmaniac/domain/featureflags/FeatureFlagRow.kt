package com.thomaskioko.tvmaniac.domain.featureflags

import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource

public data class FeatureFlagRow(
    val featureFlag: FeatureFlag,
    val value: Boolean,
    val featureFlagSource: FeatureFlagSource,
)
