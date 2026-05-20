package com.thomaskioko.tvmaniac.watchlist.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingDao
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingRepository
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.followedshows.UnfollowShowInteractor
import com.thomaskioko.tvmaniac.domain.watchlist.FetchMissingShowsInteractor
import com.thomaskioko.tvmaniac.domain.watchlist.ObserveUpNextSectionsInteractor
import com.thomaskioko.tvmaniac.domain.watchlist.ObserveWatchlistSectionsInteractor
import com.thomaskioko.tvmaniac.domain.watchlist.SyncWatchedShowInteractor
import com.thomaskioko.tvmaniac.domain.watchlist.UpNextSectionsMapper
import com.thomaskioko.tvmaniac.domain.watchlist.WatchlistSyncInteractor
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsRepository
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.testing.NoOpNavigator
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
import com.thomaskioko.tvmaniac.upnext.testing.FakeUpNextRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.watchlist.testing.FakeWatchlistRepository
import kotlinx.coroutines.test.UnconfinedTestDispatcher

class FakeWatchlistPresenterBuilder {
    val repository = FakeWatchlistRepository()
    val episodeRepository = FakeEpisodeRepository()
    val upNextRepository = FakeUpNextRepository()
    val dateTimeProvider = FakeDateTimeProvider()
    val showDetailsRepository = FakeShowDetailsRepository()
    val seasonDetailsRepository = FakeSeasonDetailsRepository()
    val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()
    val continueWatchingRepository = FakeContinueWatchingRepository()
    val continueWatchingDao = FakeContinueWatchingDao()
    val syncObserver = FakeSyncObserver()

    val testDispatcher = UnconfinedTestDispatcher()

    private val fakeFollowedShowsRepository = FakeFollowedShowsRepository()
    private val fakeLogger = FakeLogger()
    private val fakeTraktActivityRepository = FakeTraktActivityRepository()

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

    private val syncWatchedShowInteractor = SyncWatchedShowInteractor(
        showDetailsRepository = showDetailsRepository,
        seasonDetailsRepository = seasonDetailsRepository,
        watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
        dispatchers = coroutineDispatcher,
    )

    private val fetchMissingShowsInteractor = FetchMissingShowsInteractor(
        continueWatchingDao = continueWatchingDao,
        syncWatchedShowInteractor = syncWatchedShowInteractor,
        dispatchers = coroutineDispatcher,
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
        traktActivityRepository = fakeTraktActivityRepository,
        continueWatchingRepository = continueWatchingRepository,
        fetchMissingShowsInteractor = fetchMissingShowsInteractor,
        syncObserver = syncObserver,
        dispatchers = coroutineDispatcher,
    )

    fun create(
        componentContext: ComponentContext,
        navigator: Navigator = NoOpNavigator(),
    ): WatchlistPresenter = WatchlistPresenter(
        componentContext = componentContext,
        navigator = navigator,
        repository = repository,
        unfollowShowInteractor = UnfollowShowInteractor(fakeFollowedShowsRepository),
        observeWatchlistSectionsInteractor = observeWatchlistSectionsInteractor,
        observeUpNextSectionsInteractor = observeUpNextSectionsInteractor,
        markEpisodeWatchedInteractor = fakeMarkEpisodeWatchedInteractor,
        watchlistSyncInteractor = watchlistSyncInteractor,
        syncObserver = syncObserver,
        errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
        localizer = FakeLocalizer(),
        logger = fakeLogger,
    )
}
