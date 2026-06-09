package com.thomaskioko.tvmaniac.accountmanager.implementation.di

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthManager
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds

@ContributesTo(ActivityScope::class)
public interface AuthManagerMultibindings {

    @Multibinds(allowEmpty = true)
    public fun authManagers(): Map<AccountProvider, AuthManager>
}
