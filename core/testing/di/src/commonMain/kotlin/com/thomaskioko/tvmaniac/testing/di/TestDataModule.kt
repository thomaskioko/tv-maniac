@file:Suppress("unused")

package com.thomaskioko.tvmaniac.testing.di

import com.thomakioko.tvmaniac.util.testing.FakeFormatterUtil
import com.thomaskioko.tvmaniac.core.base.di.ComputationCoroutineScope
import com.thomaskioko.tvmaniac.core.base.di.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.base.di.MainCoroutineScope
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.cast.api.CastRepository
import com.thomaskioko.tvmaniac.data.cast.testing.FakeCastRepository
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsRepository
import com.thomaskioko.tvmaniac.data.featuredshows.testing.FakeFeaturedShowsRepository
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsRepository
import com.thomaskioko.tvmaniac.data.popularshows.testing.FakePopularShowsRepository
import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsRepository
import com.thomaskioko.tvmaniac.data.recommendedshows.testing.FakeRecommendedShowsRepository
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.topratedshows.testing.FakeTopRatedShowsRepository
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import com.thomaskioko.tvmaniac.data.trendingshows.testing.FakeTrendingShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.testing.FakeUpcomingShowsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsRepository
import com.thomaskioko.tvmaniac.genre.FakeGenreRepository
import com.thomaskioko.tvmaniac.genre.GenreRepository
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.search.api.SearchRepository
import com.thomaskioko.tvmaniac.search.testing.FakeSearchRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.similar.testing.FakeSimilarShowsRepository
import com.thomaskioko.tvmaniac.testing.TestScope
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsRepository
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthManager
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.watchlist.testing.FakeWatchlistRepository
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@ContributesTo(TestScope::class)
interface TestDataModule {
    @Provides
    @SingleIn(TestScope::class)
    fun provideDatastoreRepository(): DatastoreRepository = FakeDatastoreRepository()

    @Provides
    @SingleIn(TestScope::class)
    fun provideTraktAuthManager(): TraktAuthManager = FakeTraktAuthManager()

    @Provides
    @SingleIn(TestScope::class)
    fun provideRequestManagerRepository(): RequestManagerRepository = FakeRequestManagerRepository()

    @Provides
    @SingleIn(TestScope::class)
    fun provideWatchlistRepository(): WatchlistRepository = FakeWatchlistRepository()

    @Provides
    @SingleIn(TestScope::class)
    fun provideLogger(): Logger = FakeLogger()

    @Provides
    @SingleIn(TestScope::class)
    fun provideSearchRepository(): SearchRepository = FakeSearchRepository()

    @Provides
    @SingleIn(TestScope::class)
    fun providePopularShowsRepository(): PopularShowsRepository = FakePopularShowsRepository()

    @Provides
    @SingleIn(TestScope::class)
    fun provideUpcomingShowsRepository(): UpcomingShowsRepository = FakeUpcomingShowsRepository()

    @Provides
    @SingleIn(TestScope::class)
    fun provideTrendingShowsRepository(): TrendingShowsRepository = FakeTrendingShowsRepository()

    @Provides
    @SingleIn(TestScope::class)
    fun provideTopRatedShowsRepository(): TopRatedShowsRepository = FakeTopRatedShowsRepository()

    @Provides
    @SingleIn(TestScope::class)
    fun provideFeaturedShowsRepository(): FeaturedShowsRepository = FakeFeaturedShowsRepository()

    @Provides
    @SingleIn(TestScope::class)
    fun provideRecommendedShowsRepository(): RecommendedShowsRepository =
        FakeRecommendedShowsRepository()

    @Provides
    @SingleIn(TestScope::class)
    fun provideGenreRepository(): GenreRepository = FakeGenreRepository()

    @Provides
    @SingleIn(TestScope::class)
    fun provideTrailerRepository(): TrailerRepository = FakeTrailerRepository()

    @Provides
    @SingleIn(TestScope::class)
    fun provideSeasonDetailsRepository(): SeasonDetailsRepository = FakeSeasonDetailsRepository()

    @Provides
    @SingleIn(TestScope::class)
    fun provideShowDetailsRepository(): ShowDetailsRepository = FakeShowDetailsRepository()

    @Provides
    @SingleIn(TestScope::class)
    fun provideCastRepository(): CastRepository = FakeCastRepository()

    @Provides
    @SingleIn(TestScope::class)
    fun provideSimilarShowsRepository(): SimilarShowsRepository = FakeSimilarShowsRepository()

    @Provides
    @SingleIn(TestScope::class)
    fun provideWatchProvidersRepository(): WatchProviderRepository = FakeWatchProviderRepository()

    @Provides
    @SingleIn(TestScope::class)
    fun provideSeasonsRepository(): SeasonsRepository = FakeSeasonsRepository()

    @Provides
    @SingleIn(TestScope::class)
    fun provideFormatterUtil(): FormatterUtil = FakeFormatterUtil()

    @Provides
    @SingleIn(TestScope::class)
    fun provideAppCoroutineDispatchers(): AppCoroutineDispatchers {
        return AppCoroutineDispatchers(
            io = UnconfinedTestDispatcher(),
            computation = UnconfinedTestDispatcher(),
            databaseWrite = UnconfinedTestDispatcher(),
            databaseRead = UnconfinedTestDispatcher(),
            main = UnconfinedTestDispatcher(),
        )
    }


    @Provides
    @SingleIn(TestScope::class)
    @IoCoroutineScope
    fun provideTestIoCoroutineScope(dispatchers: AppCoroutineDispatchers): CoroutineScope {
        return CoroutineScope(Job() + dispatchers.io)
    }

    @Provides
    @SingleIn(TestScope::class)
    @MainCoroutineScope
    fun provideTestMainCoroutineScope(dispatchers: AppCoroutineDispatchers): CoroutineScope {
        return CoroutineScope(Job() + dispatchers.main)
    }

    @Provides
    @SingleIn(TestScope::class)
    @ComputationCoroutineScope
    fun provideTestComputationCoroutineScope(dispatchers: AppCoroutineDispatchers): CoroutineScope {
        return CoroutineScope(Job() + dispatchers.computation)
    }
}
