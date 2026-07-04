package com.thomaskioko.tvmaniac.featureflags.flags

import io.github.thomaskioko.codegen.annotations.FeatureFlag

/**
 * Continue Watching Nitro flag. The codegen emits `ContinueWatchingNitroFlagQualifier` and
 * `ContinueWatchingNitroFlagBinding` from this anchor; consumers inject
 * `@ContinueWatchingNitroFlagQualifier FeatureFlag<Boolean>`.
 */
@FeatureFlag(
    key = "enable_continue_watching_nitro",
    title = "Progress Endpoint",
    description = "Use Trakt's internal /sync/progress/up_next_nitro call instead of the documented multi-step progress fetch.",
    defaultValue = false,
    dateAdded = "2026-05-20",
)
public object ContinueWatchingNitroFlag
