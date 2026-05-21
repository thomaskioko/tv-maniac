package com.thomaskioko.tvmaniac.watchlist.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingDao
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingRepository
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.domain.continuewatching.ObserveUpNextSectionsInteractor
import com.thomaskioko.tvmaniac.domain.continuewatching.ObserveWatchlistSectionsInteractor
import com.thomaskioko.tvmaniac.domain.continuewatching.SyncContinueWatchingInteractor
import com.thomaskioko.tvmaniac.domain.continuewatching.UpNextSectionsMapper
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.followedshows.UnfollowShowInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.SyncShowMetadataInteractor
import com.thomaskioko.tvmaniac.domain.syncactivity.SyncActivityInteractor
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.featureflags.testing.FakeFeatureFlags
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsRepository
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.testing.NoOpNavigator
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import com.thomaskioko.tvmaniac.upnext.testing.FakeUpNextRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.watchlistprefs.testing.FakeWatchlistPrefsRepository
import kotlinx.coroutines.test.UnconfinedTestDispatcher

class FakeWatchlistPresenterBuilder {
    val repository = FakeWatchlistPrefsRepository()
    val episodeRepository = FakeEpisodeRepository()
    val upNextRepository = FakeUpNextRepository()
    val dateTimeProvider = FakeDateTimeProvider()
    val showDetailsRepository = FakeShowDetailsRepository()
    val seasonDetailsRepository = FakeSeasonDetailsRepository()
    val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()
    val watchProviderRepository = FakeWatchProviderRepository()
    val continueWatchingRepository = FakeContinueWatchingRepository()
    val continueWatchingDao = FakeContinueWatchingDao()
    val syncObserver = FakeSyncObserver()
    val featureFlags = FakeFeatureFlags()

    val testDispatcher = UnconfinedTestDispatcher()

    private val fakeFollowedShowsRepository = FakeFollowedShowsRepository()
    private val fakeLogger = FakeLogger()
    private val fakeTraktActivityRepository = FakeTraktActivityRepository()
    private val fakeTraktAuthRepository = FakeTraktAuthRepository()

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

    private val syncActivityInteractor = SyncActivityInteractor(
        traktActivityRepository = fakeTraktActivityRepository,
        dispatchers = coroutineDispatcher,
    )

    private val syncShowMetadataInteractor = SyncShowMetadataInteractor(
        showDetailsRepository = showDetailsRepository,
        seasonDetailsRepository = seasonDetailsRepository,
        watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
        watchProviderRepository = watchProviderRepository,
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

    private val syncContinueWatchingInteractor = SyncContinueWatchingInteractor(
        syncActivityInteractor = syncActivityInteractor,
        continueWatchingRepository = continueWatchingRepository,
        continueWatchingDao = continueWatchingDao,
        syncShowMetadataInteractor = syncShowMetadataInteractor,
        syncObserver = syncObserver,
        dispatchers = coroutineDispatcher,
        logger = fakeLogger,
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
        syncContinueWatchingInteractor = syncContinueWatchingInteractor,
        featureFlags = featureFlags,
        syncObserver = syncObserver,
        traktAuthRepository = fakeTraktAuthRepository,
        errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
        localizer = FakeLocalizer(),
        logger = fakeLogger,
    )
}
