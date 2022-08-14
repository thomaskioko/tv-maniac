package com.thomaskioko.tvmaniac.injection

import com.thomaskioko.showdetails.ShowDetailNavigationFactory
import com.thomaskioko.tvmaniac.DiscoverNavigationFactory
import com.thomaskioko.tvmaniac.following.FollowingNavigationFactory
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.search.SearchNavigationFactory
import com.thomaskioko.tvmaniac.seasons.SeasonsNavigationFactory
import com.thomaskioko.tvmaniac.settings.SettingsNavigationFactory
import com.thomaskioko.tvmaniac.show_grid.ShowsGridNavigationFactory
import com.thomaskioko.tvmaniac.videoplayer.VideoPlayerNavigationFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface NavigationModule {

    @Singleton
    @Binds
    @IntoSet
    fun bindDiscoverNavigation(factory: DiscoverNavigationFactory): ComposeNavigationFactory

    @Singleton
    @Binds
    @IntoSet
    fun bindSearchNavigation(factory: SearchNavigationFactory): ComposeNavigationFactory

    @Singleton
    @Binds
    @IntoSet
    fun bindWatchlistNavigation(factory: FollowingNavigationFactory): ComposeNavigationFactory

    @Singleton
    @Binds
    @IntoSet
    fun bindShowDetailNavigationFactory(factory: ShowDetailNavigationFactory): ComposeNavigationFactory

    @Singleton
    @Binds
    @IntoSet
    fun bindShowsGridNavigationFactory(factory: ShowsGridNavigationFactory): ComposeNavigationFactory

    @Singleton
    @Binds
    @IntoSet
    fun bindSettingsNavigationFactory(factory: SettingsNavigationFactory): ComposeNavigationFactory

    @Singleton
    @Binds
    @IntoSet
    fun bindSeasonsNavigationFactory(factory: SeasonsNavigationFactory): ComposeNavigationFactory

    @Singleton
    @Binds
    @IntoSet
    fun bindVideoPlayerNavigationFactory(
        factory: VideoPlayerNavigationFactory
    ): ComposeNavigationFactory
}
