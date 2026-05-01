package com.thomaskioko.tvmaniac.profile.presenter.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.home.nav.HomeRoute
import com.thomaskioko.tvmaniac.home.nav.TabChild
import com.thomaskioko.tvmaniac.home.nav.TabDestination
import com.thomaskioko.tvmaniac.navigation.NavRoot
import com.thomaskioko.tvmaniac.profile.nav.ProfileRoot
import com.thomaskioko.tvmaniac.profile.presenter.ProfilePresenter
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(HomeRoute::class)
public interface ProfileTabDestinationBinding {
    public companion object {
        @Provides
        @IntoSet
        public fun provideProfileTabDestination(
            graphFactory: ProfileTabGraph.Factory,
        ): TabDestination = object : TabDestination {
            override fun matches(root: NavRoot): Boolean = root is ProfileRoot

            override fun createChild(
                root: NavRoot,
                componentContext: ComponentContext,
            ): TabChild<*> = TabChild(
                presenter = graphFactory.createProfileTabGraph(componentContext).profilePresenter,
            )
        }
    }
}
