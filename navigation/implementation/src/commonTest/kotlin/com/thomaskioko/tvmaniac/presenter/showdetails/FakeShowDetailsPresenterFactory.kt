package com.thomaskioko.tvmaniac.presenter.showdetails

import com.arkivanov.decompose.ComponentContext
import com.thomakioko.tvmaniac.util.testing.FakeFormatterUtil
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.cast.testing.FakeCastRepository
import com.thomaskioko.tvmaniac.data.recommendedshows.testing.FakeRecommendedShowsRepository
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.domain.recommendedshows.RecommendedShowsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObservableShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.similarshows.SimilarShowsInteractor
import com.thomaskioko.tvmaniac.domain.watchproviders.WatchProvidersInteractor
import com.thomaskioko.tvmaniac.presenter.showdetails.di.ShowDetailsPresenterFactory
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowSeasonDetailsParam
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import com.thomaskioko.tvmaniac.similar.testing.FakeSimilarShowsRepository
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.watchlist.testing.FakeWatchlistRepository
import kotlinx.coroutines.test.StandardTestDispatcher

class FakeShowDetailsPresenterFactory : ShowDetailsPresenterFactory {
    private val watchlistRepository = FakeWatchlistRepository()
    private val recommendedShowsRepository = FakeRecommendedShowsRepository()
    private val showDetailsRepository = FakeShowDetailsRepository()
    private val watchProviderRepository = FakeWatchProviderRepository()
    private val similarShowsRepository = FakeSimilarShowsRepository()
    private val castRepository = FakeCastRepository()
    private val seasonsRepository = FakeSeasonsRepository()
    private val trailerRepository = FakeTrailerRepository()
    private val formatterUtil = FakeFormatterUtil()
    private val logger = FakeLogger()
    private val testDispatcher = StandardTestDispatcher()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    override fun create(
        componentContext: ComponentContext,
        id: Long,
        onBack: () -> Unit,
        onNavigateToShow: (id: Long) -> Unit,
        onNavigateToSeason: (param: ShowSeasonDetailsParam) -> Unit,
        onNavigateToTrailer: (id: Long) -> Unit,
    ): ShowDetailsPresenter = ShowDetailsPresenter(
        componentContext = componentContext,
        showId = id,
        onBack = onBack,
        onNavigateToShow = onNavigateToShow,
        onNavigateToSeason = onNavigateToSeason,
        onNavigateToTrailer = onNavigateToTrailer,
        watchlistRepository = watchlistRepository,
        recommendedShowsInteractor = RecommendedShowsInteractor(
            recommendedShowsRepository = recommendedShowsRepository,
            dispatchers = coroutineDispatcher,
        ),
        showDetailsInteractor = ShowDetailsInteractor(
            showDetailsRepository = showDetailsRepository,
            dispatchers = coroutineDispatcher,
        ),
        watchProvidersInteractor = WatchProvidersInteractor(
            repository = watchProviderRepository,
            dispatchers = coroutineDispatcher,
        ),
        similarShowsInteractor = SimilarShowsInteractor(
            similarShowsRepository = similarShowsRepository,
            dispatchers = coroutineDispatcher,
        ),
        observableShowDetailsInteractor = ObservableShowDetailsInteractor(
            castRepository = castRepository,
            recommendedShowsRepository = recommendedShowsRepository,
            seasonsRepository = seasonsRepository,
            showDetailsRepository = showDetailsRepository,
            similarShowsRepository = similarShowsRepository,
            trailerRepository = trailerRepository,
            watchProviders = watchProviderRepository,
            formatterUtil = formatterUtil,
            dispatchers = coroutineDispatcher,
        ),
        logger = logger,
    )
}
