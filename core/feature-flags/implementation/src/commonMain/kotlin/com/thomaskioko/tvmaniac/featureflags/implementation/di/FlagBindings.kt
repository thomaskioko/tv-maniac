package com.thomaskioko.tvmaniac.featureflags.implementation.di

import com.thomaskioko.tvmaniac.featureflags.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.FeatureFlagFactory
import com.thomaskioko.tvmaniac.featureflags.flags.AccountSwitchFlagQualifier
import com.thomaskioko.tvmaniac.featureflags.flags.ContinueWatchingNitroFlagQualifier
import com.thomaskioko.tvmaniac.featureflags.flags.SimklLoginFlagQualifier
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.datetime.LocalDate

@ContributesTo(AppScope::class)
public interface FlagBindings {

    @Provides
    @SingleIn(AppScope::class)
    @ContinueWatchingNitroFlagQualifier
    public fun continueWatchingNitroFlag(factory: FeatureFlagFactory): FeatureFlag<Boolean> = factory.boolean(
        key = "enable_continue_watching_nitro",
        title = "Progress Endpoint",
        description = "Use Trakt's internal /sync/progress/up_next_nitro call instead of the documented multi-step progress fetch.",
        defaultValue = false,
        dateAdded = LocalDate(2026, 5, 20),
    )

    @Provides
    @IntoSet
    public fun bindContinueWatchingNitroFlag(
        @ContinueWatchingNitroFlagQualifier flag: FeatureFlag<Boolean>,
    ): FeatureFlag<Boolean> = flag

    @Provides
    @SingleIn(AppScope::class)
    @SimklLoginFlagQualifier
    public fun simklLoginFlag(factory: FeatureFlagFactory): FeatureFlag<Boolean> = factory.boolean(
        key = "simkl_login_enabled",
        title = "Simkl Login",
        description = "Show the Simkl login entry point on the settings screen.",
        defaultValue = false,
        dateAdded = LocalDate(2026, 5, 17),
    )

    @Provides
    @IntoSet
    public fun bindSimklLoginFlag(
        @SimklLoginFlagQualifier flag: FeatureFlag<Boolean>,
    ): FeatureFlag<Boolean> = flag

    @Provides
    @SingleIn(AppScope::class)
    @AccountSwitchFlagQualifier
    public fun accountSwitchFlag(factory: FeatureFlagFactory): FeatureFlag<Boolean> = factory.boolean(
        key = "enable_account_switch",
        title = "Account Switch",
        description = "Show the button to switch the active provider on the settings account screen.",
        defaultValue = false,
        dateAdded = LocalDate(2026, 6, 14),
    )

    @Provides
    @IntoSet
    public fun bindAccountSwitchFlag(
        @AccountSwitchFlagQualifier flag: FeatureFlag<Boolean>,
    ): FeatureFlag<Boolean> = flag
}
