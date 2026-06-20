package com.thomaskioko.tvmaniac.testing.integration.bindings

import com.thomaskioko.tvmaniac.featureflags.FeatureFlagsRemoteConfig
import com.thomaskioko.tvmaniac.featureflags.implementation.AndroidRemoteConfig
import com.thomaskioko.tvmaniac.featureflags.implementation.DebugRemoteConfig
import com.thomaskioko.tvmaniac.featureflags.testing.FakeFeatureFlagsRemoteConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(
    AppScope::class,
    replaces = [AndroidRemoteConfig::class, DebugRemoteConfig::class],
)
public object TestFeatureFlagsBindingContainer {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideFakeRemoteConfig(): FakeFeatureFlagsRemoteConfig = FakeFeatureFlagsRemoteConfig()

    @Provides
    public fun bindRemoteConfig(
        fake: FakeFeatureFlagsRemoteConfig,
    ): FeatureFlagsRemoteConfig = fake
}
