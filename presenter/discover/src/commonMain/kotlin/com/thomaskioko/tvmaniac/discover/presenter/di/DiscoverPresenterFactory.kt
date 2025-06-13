package com.thomaskioko.tvmaniac.discover.presenter.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.data.featuredshows.api.interactor.FeaturedShowsInteractor
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsInteractor
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsInteractor
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsInteractor
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.domain.discover.DiscoverShowsInteractor
import com.thomaskioko.tvmaniac.domain.genre.GenreShowsInteractor
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsInteractor
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

interface DiscoverPresenterFactory {
    fun create(
        componentContext: ComponentContext,
        onNavigateToShowDetails: (id: Long) -> Unit,
        onNavigateToMore: (categoryId: Long) -> Unit,
    ): DiscoverShowsPresenter
}

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, DiscoverPresenterFactory::class)
class DefaultDiscoverPresenterFactory(
    private val discoverShowsInteractor: DiscoverShowsInteractor,
    private val watchlistRepository: WatchlistRepository,
    private val featuredShowsInteractor: FeaturedShowsInteractor,
    private val topRatedShowsInteractor: TopRatedShowsInteractor,
    private val popularShowsInteractor: PopularShowsInteractor,
    private val trendingShowsInteractor: TrendingShowsInteractor,
    private val upcomingShowsInteractor: UpcomingShowsInteractor,
    private val genreShowsInteractor: GenreShowsInteractor,
    private val logger: Logger,
) : DiscoverPresenterFactory {
    override fun create(
        componentContext: ComponentContext,
        onNavigateToShowDetails: (id: Long) -> Unit,
        onNavigateToMore: (categoryId: Long) -> Unit,
    ): DiscoverShowsPresenter = DiscoverShowsPresenter(
        componentContext = componentContext,
        onNavigateToShowDetails = onNavigateToShowDetails,
        onNavigateToMore = onNavigateToMore,
        discoverShowsInteractor = discoverShowsInteractor,
        watchlistRepository = watchlistRepository,
        featuredShowsInteractor = featuredShowsInteractor,
        topRatedShowsInteractor = topRatedShowsInteractor,
        popularShowsInteractor = popularShowsInteractor,
        trendingShowsInteractor = trendingShowsInteractor,
        upcomingShowsInteractor = upcomingShowsInteractor,
        genreShowsInteractor = genreShowsInteractor,
        logger = logger,
    )
}
