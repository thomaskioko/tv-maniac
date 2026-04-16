package com.thomaskioko.tvmaniac.profile.presenter.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.home.nav.scope.HomeScreenScope
import com.thomaskioko.tvmaniac.profile.nav.scope.ProfileTabScope
import com.thomaskioko.tvmaniac.profile.presenter.ProfilePresenter
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(ProfileTabScope::class)
public interface ProfileTabGraph {
    public val profilePresenter: ProfilePresenter

    @ContributesTo(HomeScreenScope::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun createProfileTabGraph(
            @Provides componentContext: ComponentContext,
        ): ProfileTabGraph
    }
}
