package com.thomaskioko.tvmaniac.presenter.moreshows

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.data.popularshows.testing.FakePopularShowsRepository
import com.thomaskioko.tvmaniac.data.topratedshows.testing.FakeTopRatedShowsRepository
import com.thomaskioko.tvmaniac.data.trendingshows.testing.FakeTrendingShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.testing.FakeUpcomingShowsRepository
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsPresenter
import com.thomaskioko.tvmaniac.moreshows.presentation.di.MoreShowsPresenterFactory

class FakeMoreShowsPresenterFactory : MoreShowsPresenterFactory {
    private val popularShowsRepository = FakePopularShowsRepository()
    private val upcomingShowsRepository = FakeUpcomingShowsRepository()
    private val trendingShowsRepository = FakeTrendingShowsRepository()
    private val topRatedShowsRepository = FakeTopRatedShowsRepository()

    override fun create(
        componentContext: ComponentContext,
        id: Long,
        onBack: () -> Unit,
        onNavigateToShowDetails: (id: Long) -> Unit,
    ): MoreShowsPresenter = MoreShowsPresenter(
        componentContext = componentContext,
        categoryId = id,
        onBack = onBack,
        onNavigateToShowDetails = onNavigateToShowDetails,
        popularShowsRepository = popularShowsRepository,
        upcomingShowsRepository = upcomingShowsRepository,
        trendingShowsRepository = trendingShowsRepository,
        topRatedShowsRepository = topRatedShowsRepository,
    )
}
