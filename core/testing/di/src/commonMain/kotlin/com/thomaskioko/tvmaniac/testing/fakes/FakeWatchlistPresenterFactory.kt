package com.thomaskioko.tvmaniac.testing.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.domain.watchlist.WatchlistInteractor
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import com.thomaskioko.tvmaniac.testing.TestScope
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistPresenter
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class)
class FakeWatchlistPresenterFactory(
    private val repository: WatchlistRepository,
    private val refreshWatchlistInteractor: WatchlistInteractor,
    private val logger: Logger,
) : WatchlistPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        navigateToShowDetails: (showDetails: Long) -> Unit,
    ): WatchlistPresenter = WatchlistPresenter(
        componentContext = componentContext,
        repository = repository,
        refreshWatchlistInteractor = refreshWatchlistInteractor,
        navigateToShowDetails = navigateToShowDetails,
        logger = logger,
    )
}
