package com.thomaskioko.tvmaniac.featureflags.flags

import com.thomaskioko.tvmaniac.featureflags.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.FeatureFlagsRemoteConfig
import com.thomaskioko.tvmaniac.featureflags.RemoteFlag
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import kotlinx.datetime.LocalDate

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class, binding = binding<FeatureFlag>())
public class ContinueWatchingNitroFlag(
    remote: FeatureFlagsRemoteConfig,
) : RemoteFlag(
    key = "enable_continue_watching_nitro",
    title = "Progress Endpoint",
    description = "Use Trakt's internal /sync/progress/up_next_nitro call instead of the documented multi-step progress fetch.",
    dateAdded = LocalDate(2026, 5, 20),
    defaultValue = false,
    remote = remote,
)
