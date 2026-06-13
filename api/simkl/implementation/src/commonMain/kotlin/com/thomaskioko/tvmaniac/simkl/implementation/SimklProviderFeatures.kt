package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProviderKey
import com.thomaskioko.tvmaniac.accountmanager.api.ProviderFeatures
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding

@SingleIn(AppScope::class)
@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<
        @AccountProviderKey(AccountProvider.SIMKL)
        ProviderFeatures,
        >(),
)
public class SimklProviderFeatures : ProviderFeatures {
    override val supportsContinueWatchingFetch: Boolean = false
    override val supportsFavorites: Boolean = false
    override val supportsLists: Boolean = false
    override val supportsCalendar: Boolean = false
}
