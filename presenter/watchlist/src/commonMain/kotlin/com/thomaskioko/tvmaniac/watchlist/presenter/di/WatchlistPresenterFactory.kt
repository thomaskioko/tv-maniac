package com.thomaskioko.tvmaniac.watchlist.presenter.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistPresenter
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

interface WatchlistPresenterFactory {
    fun create(
        componentContext: ComponentContext,
        navigateToShowDetails: (showDetails: Long) -> Unit,
    ): WatchlistPresenter
}

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, WatchlistPresenterFactory::class)
class DefaultWatchlistPresenterFactory(
    private val repository: WatchlistRepository,
) : WatchlistPresenterFactory {
    override fun create(
        componentContext: ComponentContext,
        navigateToShowDetails: (showDetails: Long) -> Unit,
    ): WatchlistPresenter = WatchlistPresenter(
        componentContext = componentContext,
        navigateToShowDetails = navigateToShowDetails,
        repository = repository,
    )
}
