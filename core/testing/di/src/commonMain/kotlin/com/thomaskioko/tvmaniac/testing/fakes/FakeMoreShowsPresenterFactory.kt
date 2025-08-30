package com.thomaskioko.tvmaniac.testing.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsRepository
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsRepository
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsPresenter
import com.thomaskioko.tvmaniac.testing.TestScope
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsRepository
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class)
class FakeMoreShowsPresenterFactory(
    private val popularShowsRepository: PopularShowsRepository,
    private val upcomingShowsRepository: UpcomingShowsRepository,
    private val trendingShowsRepository: TrendingShowsRepository,
    private val topRatedShowsRepository: TopRatedShowsRepository,
) : MoreShowsPresenter.Factory {
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
