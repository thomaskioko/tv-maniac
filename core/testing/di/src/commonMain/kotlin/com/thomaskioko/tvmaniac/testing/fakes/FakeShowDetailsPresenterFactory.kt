package com.thomaskioko.tvmaniac.testing.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.domain.recommendedshows.RecommendedShowsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObservableShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.similarshows.SimilarShowsInteractor
import com.thomaskioko.tvmaniac.domain.watchproviders.WatchProvidersInteractor
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowSeasonDetailsParam
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import com.thomaskioko.tvmaniac.testing.TestScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class)
class FakeShowDetailsPresenterFactory(
    private val watchlistRepository: WatchlistRepository,
    private val recommendedShowsInteractor: RecommendedShowsInteractor,
    private val showDetailsInteractor: ShowDetailsInteractor,
    private val similarShowsInteractor: SimilarShowsInteractor,
    private val watchProvidersInteractor: WatchProvidersInteractor,
    private val observableShowDetailsInteractor: ObservableShowDetailsInteractor,
    private val logger: Logger,
) : ShowDetailsPresenter.Factory {
    override fun create(
        componentContext: ComponentContext,
        showId: Long,
        onBack: () -> Unit,
        onNavigateToShow: (Long) -> Unit,
        onNavigateToSeason: (ShowSeasonDetailsParam) -> Unit,
        onNavigateToTrailer: (Long) -> Unit,
    ): ShowDetailsPresenter = ShowDetailsPresenter(
        componentContext = componentContext,
        showId = showId,
        onBack = onBack,
        onNavigateToShow = onNavigateToShow,
        onNavigateToSeason = onNavigateToSeason,
        onNavigateToTrailer = onNavigateToTrailer,
        watchlistRepository = watchlistRepository,
        recommendedShowsInteractor = recommendedShowsInteractor,
        showDetailsInteractor = showDetailsInteractor,
        similarShowsInteractor = similarShowsInteractor,
        watchProvidersInteractor = watchProvidersInteractor,
        observableShowDetailsInteractor = observableShowDetailsInteractor,
        logger = logger,
    )
}
