package com.thomaskioko.tvmaniac.moreshows.presentation.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsRepository
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsRepository
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsPresenter
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsRepository
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

interface MoreShowsPresenterFactory {
    fun create(
        componentContext: ComponentContext,
        id: Long,
        onBack: () -> Unit,
        onNavigateToShowDetails: (id: Long) -> Unit,
    ): MoreShowsPresenter
}

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, MoreShowsPresenterFactory::class)
class DefaultMoreShowsPresenterFactory(
    private val popularShowsRepository: PopularShowsRepository,
    private val upcomingShowsRepository: UpcomingShowsRepository,
    private val trendingShowsRepository: TrendingShowsRepository,
    private val topRatedShowsRepository: TopRatedShowsRepository,
) : MoreShowsPresenterFactory {
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
