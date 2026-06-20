package com.thomaskioko.tvmaniac.featureflags.flags

import io.github.thomaskioko.codegen.annotations.FeatureFlag

/**
 * Account Switch flag. The codegen emits `AccountSwitchFlagQualifier` and `AccountSwitchFlagBinding`
 * from this anchor; consumers inject `@AccountSwitchFlagQualifier FeatureFlag<Boolean>`.
 */
@FeatureFlag(
    key = "enable_account_switch",
    title = "Account Switch",
    description = "Show the button to switch the active provider on the settings account screen.",
    defaultValue = false,
    dateAdded = "2026-06-14",
)
public object AccountSwitchFlag
