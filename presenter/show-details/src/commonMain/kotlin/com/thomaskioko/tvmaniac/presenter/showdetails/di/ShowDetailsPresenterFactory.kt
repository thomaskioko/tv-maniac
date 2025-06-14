package com.thomaskioko.tvmaniac.presenter.showdetails.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.domain.recommendedshows.RecommendedShowsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObservableShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.similarshows.SimilarShowsInteractor
import com.thomaskioko.tvmaniac.domain.watchproviders.WatchProvidersInteractor
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowSeasonDetailsParam
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

interface ShowDetailsPresenterFactory {
    fun create(
        componentContext: ComponentContext,
        id: Long,
        onBack: () -> Unit,
        onNavigateToShow: (id: Long) -> Unit,
        onNavigateToSeason: (param: ShowSeasonDetailsParam) -> Unit,
        onNavigateToTrailer: (id: Long) -> Unit,
    ): ShowDetailsPresenter
}

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, ShowDetailsPresenterFactory::class)
class DefaultShowDetailsPresenterFactory(
    private val watchlistRepository: WatchlistRepository,
    private val recommendedShowsInteractor: RecommendedShowsInteractor,
    private val showDetailsInteractor: ShowDetailsInteractor,
    private val similarShowsInteractor: SimilarShowsInteractor,
    private val watchProvidersInteractor: WatchProvidersInteractor,
    private val observableShowDetailsInteractor: ObservableShowDetailsInteractor,
    private val logger: Logger,
) : ShowDetailsPresenterFactory {
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
        recommendedShowsInteractor = recommendedShowsInteractor,
        showDetailsInteractor = showDetailsInteractor,
        similarShowsInteractor = similarShowsInteractor,
        watchProvidersInteractor = watchProvidersInteractor,
        observableShowDetailsInteractor = observableShowDetailsInteractor,
        logger = logger,
    )
}
