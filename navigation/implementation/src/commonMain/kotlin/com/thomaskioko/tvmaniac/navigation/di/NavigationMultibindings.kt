package com.thomaskioko.tvmaniac.navigation.di

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.NavDestination
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds

@ContributesTo(ActivityScope::class)
public interface NavigationMultibindings {
    @Multibinds
    public fun navDestinations(): Set<NavDestination>
}
