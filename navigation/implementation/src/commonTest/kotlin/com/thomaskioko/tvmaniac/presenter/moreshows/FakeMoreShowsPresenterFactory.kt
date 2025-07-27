package com.thomaskioko.tvmaniac.presenter.moreshows

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.data.popularshows.testing.FakePopularShowsRepository
import com.thomaskioko.tvmaniac.data.topratedshows.testing.FakeTopRatedShowsRepository
import com.thomaskioko.tvmaniac.data.trendingshows.testing.FakeTrendingShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.testing.FakeUpcomingShowsRepository
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsPresenter

class FakeMoreShowsPresenterFactory : MoreShowsPresenter.Factory {
    private val popularShowsRepository = FakePopularShowsRepository()
    private val upcomingShowsRepository = FakeUpcomingShowsRepository()
    private val trendingShowsRepository = FakeTrendingShowsRepository()
    private val topRatedShowsRepository = FakeTopRatedShowsRepository()

    override fun invoke(
        componentContext: ComponentContext,
        categoryId: Long,
        onBack: () -> Unit,
        onNavigateToShowDetails: (id: Long) -> Unit,
    ): MoreShowsPresenter = MoreShowsPresenter(
        componentContext = componentContext,
        categoryId = categoryId,
        onBack = onBack,
        onNavigateToShowDetails = onNavigateToShowDetails,
        popularShowsRepository = popularShowsRepository,
        upcomingShowsRepository = upcomingShowsRepository,
        trendingShowsRepository = trendingShowsRepository,
        topRatedShowsRepository = topRatedShowsRepository,
    )
}
