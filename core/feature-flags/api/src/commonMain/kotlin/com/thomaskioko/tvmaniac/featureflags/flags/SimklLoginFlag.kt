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
public class SimklLoginFlag(
    remote: FeatureFlagsRemoteConfig,
) : RemoteFlag(
    key = "simkl_login_enabled",
    title = "Simkl Login",
    description = "Show the Simkl login entry point on the settings screen.",
    dateAdded = LocalDate(2026, 5, 17),
    defaultValue = false,
    remote = remote,
)
