package com.thomaskioko.tvmaniac.accountmanager.implementation.di

import com.thomaskioko.tvmaniac.accountmanager.api.AuthManager
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds

/**
 * Declares the [AuthManager] multibinding each provider contributes into. Contributed at
 * [ActivityScope] because the web flow needs the host Activity. Allowed empty so test graphs that
 * never exercise sign-in resolve the set without a provider adapter.
 */
@ContributesTo(ActivityScope::class)
public interface AuthManagerMultibindings {
    @Multibinds(allowEmpty = true)
    public fun authManagers(): Set<AuthManager>
}
