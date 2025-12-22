package com.thomaskioko.tvmaniac.watchlist.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.watchlist.ObserveUpNextSectionsInteractor
import com.thomaskioko.tvmaniac.domain.watchlist.ObserveWatchlistSectionsInteractor
import com.thomaskioko.tvmaniac.domain.watchlist.WatchlistInteractor
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.watchlist.testing.FakeWatchlistRepository
import kotlinx.coroutines.test.UnconfinedTestDispatcher

class FakeWatchlistPresenterFactory : WatchlistPresenter.Factory {
    val repository = FakeWatchlistRepository()
    val episodeRepository = FakeEpisodeRepository()
    val dateTimeProvider = FakeDateTimeProvider()

    val testDispatcher = UnconfinedTestDispatcher()

    private val fakeRefreshWatchlistInteractor = WatchlistInteractor(
        repository,
    )

    private val fakeMarkEpisodeWatchedInteractor = MarkEpisodeWatchedInteractor(
        episodeRepository = episodeRepository,
    )

    private val observeWatchlistSectionsInteractor = ObserveWatchlistSectionsInteractor(
        watchlistRepository = repository,
        episodeRepository = episodeRepository,
        dateTimeProvider = dateTimeProvider,
    )

    private val observeUpNextSectionsInteractor = ObserveUpNextSectionsInteractor(
        watchlistRepository = repository,
        episodeRepository = episodeRepository,
        dateTimeProvider = dateTimeProvider,
    )

    override fun invoke(
        componentContext: ComponentContext,
        navigateToShowDetails: (showDetails: Long) -> Unit,
        navigateToSeason: (showId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
    ): WatchlistPresenter = DefaultWatchlistPresenter(
        componentContext = componentContext,
        navigateToShowDetails = navigateToShowDetails,
        navigateToSeason = navigateToSeason,
        repository = repository,
        observeWatchlistSectionsInteractor = observeWatchlistSectionsInteractor,
        observeUpNextSectionsInteractor = observeUpNextSectionsInteractor,
        refreshWatchlistInteractor = fakeRefreshWatchlistInteractor,
        markEpisodeWatchedInteractor = fakeMarkEpisodeWatchedInteractor,
        dateTimeProvider = dateTimeProvider,
        logger = FakeLogger(),
    )
}
