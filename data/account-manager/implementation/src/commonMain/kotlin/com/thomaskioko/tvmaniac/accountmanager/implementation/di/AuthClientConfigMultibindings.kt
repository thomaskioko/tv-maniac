package com.thomaskioko.tvmaniac.accountmanager.implementation.di

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthClientConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds

@ContributesTo(AppScope::class)
public interface AuthClientConfigMultibindings {

    @Multibinds(allowEmpty = true)
    public fun authClientConfigs(): Map<AccountProvider, AuthClientConfig>
}
