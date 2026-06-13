package com.thomaskioko.tvmaniac.accountmanager.implementation.di

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.NoProviderFeatures
import com.thomaskioko.tvmaniac.accountmanager.api.ProviderFeatures
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
public interface ProviderFeaturesMultibindings {

    @Multibinds(allowEmpty = true)
    public fun providerFeatures(): Map<AccountProvider, ProviderFeatures>
}

@BindingContainer
@ContributesTo(AppScope::class)
public object ActiveProviderFeaturesBindingContainer {
    @Provides
    public fun activeProviderFeatures(
        features: Map<AccountProvider, ProviderFeatures>,
        accountManager: AccountManager,
    ): ProviderFeatures = accountManager.getActiveProvider()?.let { features[it] } ?: NoProviderFeatures
}
