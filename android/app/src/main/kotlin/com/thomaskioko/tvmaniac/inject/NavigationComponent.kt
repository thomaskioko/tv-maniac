package com.thomaskioko.tvmaniac.inject

import com.thomaskioko.showdetails.ShowDetailNavigationFactory
import com.thomaskioko.tvmaniac.base.scope.ApplicationScope
import com.thomaskioko.tvmaniac.discover.DiscoverNavigationFactory
import com.thomaskioko.tvmaniac.following.FollowingNavigationFactory
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.profile.ProfileNavigationFactory
import com.thomaskioko.tvmaniac.search.SearchNavigationFactory
import com.thomaskioko.tvmaniac.seasondetails.SeasonDetailsNavigationFactory
import com.thomaskioko.tvmaniac.settings.SettingsNavigationFactory
import com.thomaskioko.tvmaniac.show_grid.ShowsGridNavigationFactory
import com.thomaskioko.tvmaniac.videoplayer.TrailerNavigationFactory
import me.tatarka.inject.annotations.Provides

interface NavigationComponent {

    @ApplicationScope
    @Provides
    fun provideSettingsNavigationFactory(
        bind: SettingsNavigationFactory
    ): ComposeNavigationFactory = bind

    @ApplicationScope
    @Provides
    fun provideShowDetailNavigationFactory(
        bind: ShowDetailNavigationFactory
    ): ComposeNavigationFactory = bind

    @Provides
    fun bindDiscoverNavigation(
        factory: DiscoverNavigationFactory
    ): ComposeNavigationFactory = factory

    @Provides
    fun bindSearchNavigation(
        factory: SearchNavigationFactory
    ): ComposeNavigationFactory = factory

    @Provides
    fun bindWatchlistNavigation(
        factory: FollowingNavigationFactory
    ): ComposeNavigationFactory = factory

    @Provides
    fun bindShowDetailNavigationFactory(
        factory: ShowDetailNavigationFactory
    )
            : ComposeNavigationFactory = factory

    @Provides
    fun bindShowsGridNavigationFactory(
        factory: ShowsGridNavigationFactory
    ): ComposeNavigationFactory = factory

    @Provides
    fun bindSettingsNavigationFactory(
        factory: SettingsNavigationFactory
    ): ComposeNavigationFactory = factory

    @Provides
    fun bindSeasonsNavigationFactory(
        factory: SeasonDetailsNavigationFactory
    ): ComposeNavigationFactory = factory

    @Provides
    fun bindProfileNavigationFactory(
        factory: ProfileNavigationFactory
    ): ComposeNavigationFactory = factory

    @Provides
    fun bindTrailerNavigationFactory(
        factory: TrailerNavigationFactory
    ): ComposeNavigationFactory = factory
}