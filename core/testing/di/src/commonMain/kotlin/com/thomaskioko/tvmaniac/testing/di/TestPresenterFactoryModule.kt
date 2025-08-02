package com.thomaskioko.tvmaniac.testing.di

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsPresenter
import com.thomaskioko.tvmaniac.navigation.DefaultRootPresenter
import com.thomaskioko.tvmaniac.presenter.home.DefaultHomePresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsPresenter
import com.thomaskioko.tvmaniac.testing.di.fakes.FakeDiscoverPresenterFactory
import com.thomaskioko.tvmaniac.testing.di.fakes.FakeMoreShowsPresenterFactory
import com.thomaskioko.tvmaniac.testing.di.fakes.FakeSearchPresenterFactory
import com.thomaskioko.tvmaniac.testing.di.fakes.FakeSeasonDetailsPresenterFactory
import com.thomaskioko.tvmaniac.testing.di.fakes.FakeSettingsPresenterFactory
import com.thomaskioko.tvmaniac.testing.di.fakes.FakeShowDetailsPresenterFactory
import com.thomaskioko.tvmaniac.testing.di.fakes.FakeTrailersPresenterFactory
import com.thomaskioko.tvmaniac.testing.di.fakes.FakeWatchlistPresenterFactory
import com.thomaskioko.tvmaniac.presenter.trailers.TrailersPresenter
import com.thomaskioko.tvmaniac.search.presenter.SearchShowsPresenter
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsPresenter
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPresenter
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistPresenter
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(TestScope::class)
interface TestPresenterFactoryModule {

    @Provides
    @SingleIn(TestScope::class)
    fun provideDiscoverPresenterFactory(): DiscoverShowsPresenter.Factory = FakeDiscoverPresenterFactory()

    @Provides
    @SingleIn(TestScope::class)
    fun provideSearchPresenterFactory(): SearchShowsPresenter.Factory = FakeSearchPresenterFactory()

    @Provides
    @SingleIn(TestScope::class)
    fun provideSettingsPresenterFactory(): SettingsPresenter.Factory = FakeSettingsPresenterFactory()

    @Provides
    @SingleIn(TestScope::class)
    fun provideWatchlistPresenterFactory(): WatchlistPresenter.Factory = FakeWatchlistPresenterFactory()

    @Provides
    @SingleIn(TestScope::class)
    fun provideShowDetailsPresenterFactory(): ShowDetailsPresenter.Factory = FakeShowDetailsPresenterFactory()

    @Provides
    @SingleIn(TestScope::class)
    fun provideMoreShowsPresenterFactory(): MoreShowsPresenter.Factory = FakeMoreShowsPresenterFactory()

    @Provides
    @SingleIn(TestScope::class)
    fun provideSeasonDetailsPresenterFactory(): SeasonDetailsPresenter.Factory = FakeSeasonDetailsPresenterFactory()

    @Provides
    @SingleIn(TestScope::class)
    fun provideTrailersPresenterFactory(): TrailersPresenter.Factory = FakeTrailersPresenterFactory()

    @Provides
    @SingleIn(TestScope::class)
    fun provideHomePresenterFactory(
        discoverPresenterFactory: DiscoverShowsPresenter.Factory,
        watchlistPresenterFactory: WatchlistPresenter.Factory,
        searchPresenterFactory: SearchShowsPresenter.Factory,
        settingsPresenterFactory: SettingsPresenter.Factory,
        traktAuthManager: TraktAuthManager,
    ): DefaultHomePresenter.Factory = DefaultHomePresenter.Factory(
        discoverPresenterFactory = discoverPresenterFactory,
        watchlistPresenterFactory = watchlistPresenterFactory,
        searchPresenterFactory = searchPresenterFactory,
        settingsPresenterFactory = settingsPresenterFactory,
        traktAuthManager = traktAuthManager,
    )

    @Provides
    @SingleIn(TestScope::class)
    fun provideRootPresenterFactory(
        homePresenterFactory: DefaultHomePresenter.Factory,
        moreShowsPresenterFactory: MoreShowsPresenter.Factory,
        showDetailsPresenterFactory: ShowDetailsPresenter.Factory,
        seasonDetailsPresenterFactory: SeasonDetailsPresenter.Factory,
        trailersPresenterFactory: TrailersPresenter.Factory,
        datastoreRepository: DatastoreRepository,
    ): DefaultRootPresenter.Factory = DefaultRootPresenter.Factory(
        homePresenterFactory = homePresenterFactory,
        moreShowsPresenterFactory = moreShowsPresenterFactory,
        showDetailsPresenterFactory = showDetailsPresenterFactory,
        seasonDetailsPresenterFactory = seasonDetailsPresenterFactory,
        trailersPresenterFactory = trailersPresenterFactory,
        datastoreRepository = datastoreRepository,
    )
}
