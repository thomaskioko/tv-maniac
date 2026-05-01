package com.thomaskioko.tvmaniac.profile.presenter.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.home.nav.HomeRoute
import com.thomaskioko.tvmaniac.profile.nav.ProfileRoot
import com.thomaskioko.tvmaniac.profile.presenter.ProfilePresenter
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(ProfileRoot::class)
public interface ProfileTabGraph {
    public val profilePresenter: ProfilePresenter

    @ContributesTo(HomeRoute::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun createProfileTabGraph(@Provides componentContext: ComponentContext): ProfileTabGraph
    }
}
