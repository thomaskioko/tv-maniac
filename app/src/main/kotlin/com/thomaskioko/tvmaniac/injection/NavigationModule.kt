package com.thomaskioko.tvmaniac.injection

import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.ui.detail.ShowDetailNavigationFactory
import com.thomaskioko.tvmaniac.ui.discover.DiscoverNavigationFactory
import com.thomaskioko.tvmaniac.ui.search.SearchNavigationFactory
import com.thomaskioko.tvmaniac.ui.watchlist.WatchlistNavigationFactory
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
    fun bindWatchlistNavigation(factory: WatchlistNavigationFactory): ComposeNavigationFactory


    @Singleton
    @Binds
    @IntoSet
    fun bindShowDetailNavigationFactory(factory: ShowDetailNavigationFactory): ComposeNavigationFactory
}