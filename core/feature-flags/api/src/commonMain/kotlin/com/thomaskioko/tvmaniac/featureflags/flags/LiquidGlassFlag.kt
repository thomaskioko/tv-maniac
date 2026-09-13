package com.thomaskioko.tvmaniac.featureflags.flags

import io.github.thomaskioko.codegen.annotations.FeatureFlag
import io.github.thomaskioko.codegen.annotations.Platform

@FeatureFlag(
    key = "enable_liquid_glass",
    title = "Liquid Glass",
    description = "Apply Liquid Glass design.",
    defaultValue = false,
    dateAdded = "2026-09-11",
    platform = Platform.IOS,
)
public object LiquidGlassFlag
