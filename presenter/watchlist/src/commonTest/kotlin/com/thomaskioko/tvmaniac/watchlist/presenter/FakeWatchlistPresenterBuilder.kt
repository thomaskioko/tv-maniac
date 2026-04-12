package com.thomaskioko.tvmaniac.watchlist.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.library.testing.FakeLibraryRepository
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.followedshows.UnfollowShowInteractor
import com.thomaskioko.tvmaniac.domain.watchlist.ObserveUpNextSectionsInteractor
import com.thomaskioko.tvmaniac.domain.watchlist.ObserveWatchlistSectionsInteractor
import com.thomaskioko.tvmaniac.domain.watchlist.UpNextSectionsMapper
import com.thomaskioko.tvmaniac.domain.watchlist.WatchlistSyncInteractor
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.upnext.testing.FakeUpNextRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.watchlist.testing.FakeWatchlistRepository
import kotlinx.coroutines.test.UnconfinedTestDispatcher

class FakeWatchlistPresenterBuilder {
    val repository = FakeWatchlistRepository()
    val episodeRepository = FakeEpisodeRepository()
    val upNextRepository = FakeUpNextRepository()
    val dateTimeProvider = FakeDateTimeProvider()

    val testDispatcher = UnconfinedTestDispatcher()

    private val fakeFollowedShowsRepository = FakeFollowedShowsRepository()
    private val fakeLogger = FakeLogger()
    private val fakeTraktActivityRepository = FakeTraktActivityRepository()
    private val fakeLibraryRepository = FakeLibraryRepository()

    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val fakeMarkEpisodeWatchedInteractor = MarkEpisodeWatchedInteractor(
        episodeRepository = episodeRepository,
    )

    private val observeWatchlistSectionsInteractor = ObserveWatchlistSectionsInteractor(
        upNextRepository = upNextRepository,
        dateTimeProvider = dateTimeProvider,
    )

    private val upNextSectionsMapper = UpNextSectionsMapper(
        dateTimeProvider = dateTimeProvider,
    )

    private val observeUpNextSectionsInteractor = ObserveUpNextSectionsInteractor(
        upNextRepository = upNextRepository,
        mapper = upNextSectionsMapper,
    )

    private val watchlistSyncInteractor = WatchlistSyncInteractor(
        libraryRepository = fakeLibraryRepository,
        traktActivityRepository = fakeTraktActivityRepository,
        dispatchers = coroutineDispatcher,
    )

    fun create(
        componentContext: ComponentContext,
        navigator: WatchlistNavigator = object : WatchlistNavigator {
            override fun showDetails(traktId: Long) {}
            override fun showSeasonDetails(showTraktId: Long, seasonId: Long, seasonNumber: Long) {}
        },
    ): WatchlistPresenter = WatchlistPresenter(
        componentContext = componentContext,
        navigator = navigator,
        repository = repository,
        unfollowShowInteractor = UnfollowShowInteractor(fakeFollowedShowsRepository),
        observeWatchlistSectionsInteractor = observeWatchlistSectionsInteractor,
        observeUpNextSectionsInteractor = observeUpNextSectionsInteractor,
        markEpisodeWatchedInteractor = fakeMarkEpisodeWatchedInteractor,
        watchlistSyncInteractor = watchlistSyncInteractor,
        errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
        logger = fakeLogger,
    )
}
