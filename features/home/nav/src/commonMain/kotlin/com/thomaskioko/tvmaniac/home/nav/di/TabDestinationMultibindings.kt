package com.thomaskioko.tvmaniac.home.nav.di

import com.thomaskioko.tvmaniac.home.nav.HomeRoute
import com.thomaskioko.tvmaniac.home.nav.TabDestination
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds

@ContributesTo(HomeRoute::class)
public interface TabDestinationMultibindings {
    @Multibinds
    public fun tabDestinations(): Set<TabDestination>
}
