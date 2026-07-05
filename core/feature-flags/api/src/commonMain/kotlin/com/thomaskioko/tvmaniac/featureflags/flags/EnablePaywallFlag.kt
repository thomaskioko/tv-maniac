package com.thomaskioko.tvmaniac.featureflags.flags

import io.github.thomaskioko.codegen.annotations.FeatureFlag

@FeatureFlag(
    key = "enable_paywall",
    title = "Enable Paywall",
    description = "Enable RevenueCat paywall integration.",
    defaultValue = false,
    dateAdded = "2026-07-05",
)
public object EnablePaywallFlag
