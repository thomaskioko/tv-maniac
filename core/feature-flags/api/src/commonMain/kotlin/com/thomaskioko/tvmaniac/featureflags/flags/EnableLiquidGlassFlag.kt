package com.thomaskioko.tvmaniac.featureflags.flags

import io.github.thomaskioko.codegen.annotations.FeatureFlag
import io.github.thomaskioko.codegen.annotations.Platform

/**
 * Liquid Glass flag, scoped to iOS via [Platform.IOS]. The codegen emits its qualifier and binding
 * into the iOS graph only, so it surfaces on the iOS debug screen and is absent from the Android
 * binary. The anchor stays in commonMain; the platform field does the scoping.
 */
@FeatureFlag(
    key = "enable_liquid_glass",
    title = "Liquid Glass",
    description = "Render the iOS UI with the Liquid Glass material.",
    defaultValue = false,
    dateAdded = "2026-06-19",
    platform = Platform.IOS,
)
public object EnableLiquidGlassFlag
