package com.thomaskioko.tvmaniac.home.nav.di

import com.thomaskioko.tvmaniac.home.nav.TabDestination
import com.thomaskioko.tvmaniac.home.nav.scope.HomeScreenScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds

@ContributesTo(HomeScreenScope::class)
public interface TabDestinationMultibindings {
    @Multibinds
    public fun tabDestinations(): Set<TabDestination>
}
