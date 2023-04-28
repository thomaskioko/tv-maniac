package com.thomaskioko.tvmaniac.inject

import com.thomaskioko.showdetails.ShowDetailNavigationFactory
import com.thomaskioko.tvmaniac.discover.DiscoverNavigationFactory
import com.thomaskioko.tvmaniac.following.FollowingNavigationFactory
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.profile.ProfileNavigationFactory
import com.thomaskioko.tvmaniac.search.SearchNavigationFactory
import com.thomaskioko.tvmaniac.seasondetails.SeasonDetailsNavigationFactory
import com.thomaskioko.tvmaniac.settings.SettingsNavigationFactory
import com.thomaskioko.tvmaniac.showsgrid.ShowsGridNavigationFactory
import com.thomaskioko.tvmaniac.videoplayer.TrailerNavigationFactory
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface NavigationComponent {

    @Provides
    @IntoSet
    fun provideSettingsNavigationFactory(
        bind: SettingsNavigationFactory,
    ): ComposeNavigationFactory = bind

    @Provides
    @IntoSet
    fun provideShowDetailNavigationFactory(
        bind: ShowDetailNavigationFactory,
    ): ComposeNavigationFactory = bind

    @Provides
    @IntoSet
    fun bindDiscoverNavigation(
        factory: DiscoverNavigationFactory,
    ): ComposeNavigationFactory = factory

    @Provides
    @IntoSet
    fun bindSearchNavigation(
        factory: SearchNavigationFactory,
    ): ComposeNavigationFactory = factory

    @Provides
    @IntoSet
    fun bindWatchlistNavigation(
        factory: FollowingNavigationFactory,
    ): ComposeNavigationFactory = factory

    @Provides
    @IntoSet
    fun bindShowDetailNavigationFactory(
        factory: ShowDetailNavigationFactory,
    ): ComposeNavigationFactory = factory

    @Provides
    @IntoSet
    fun bindShowsGridNavigationFactory(
        factory: ShowsGridNavigationFactory,
    ): ComposeNavigationFactory = factory

    @Provides
    @IntoSet
    fun bindSettingsNavigationFactory(
        factory: SettingsNavigationFactory,
    ): ComposeNavigationFactory = factory

    @Provides
    @IntoSet
    fun bindSeasonsNavigationFactory(
        factory: SeasonDetailsNavigationFactory,
    ): ComposeNavigationFactory = factory

    @Provides
    @IntoSet
    fun bindProfileNavigationFactory(
        factory: ProfileNavigationFactory,
    ): ComposeNavigationFactory = factory

    @Provides
    @IntoSet
    fun bindTrailerNavigationFactory(
        factory: TrailerNavigationFactory,
    ): ComposeNavigationFactory = factory
}
