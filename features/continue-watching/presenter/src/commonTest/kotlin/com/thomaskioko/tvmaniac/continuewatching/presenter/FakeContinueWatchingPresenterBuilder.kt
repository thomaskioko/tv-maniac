package com.thomaskioko.tvmaniac.continuewatching.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingRepository
import com.thomaskioko.tvmaniac.core.base.coroutines.FakeAppScopeLauncher
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.library.testing.FakeLibraryRepository
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
import com.thomaskioko.tvmaniac.featureflags.testing.FakeFeatureFlag
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsRepository
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.testing.NoOpNavigator
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
import com.thomaskioko.tvmaniac.upnext.testing.FakeUpNextRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.watchlistprefs.testing.FakeWatchlistPrefsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.UnconfinedTestDispatcher

class FakeContinueWatchingPresenterBuilder {
    val repository = FakeWatchlistPrefsRepository()
    val episodeRepository = FakeEpisodeRepository()
    val upNextRepository = FakeUpNextRepository()
    val dateTimeProvider = FakeDateTimeProvider()
    val showDetailsRepository = FakeShowDetailsRepository()
    val seasonDetailsRepository = FakeSeasonDetailsRepository()
    val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()
    val watchProviderRepository = FakeWatchProviderRepository()
    val continueWatchingRepository = FakeContinueWatchingRepository()
    val requestManagerRepository = FakeRequestManagerRepository(initialRequestValid = false)
    val syncObserver = FakeSyncObserver()
    val nitroFlag = FakeFeatureFlag(initial = false)
    val localizer = FakeLocalizer()

    val testDispatcher = UnconfinedTestDispatcher()

    private val appCoroutineScope = CoroutineScope(testDispatcher + SupervisorJob())
    private val fakeFollowedShowsRepository = FakeFollowedShowsRepository()
    private val fakeLogger = FakeLogger()
    private val fakeTraktActivityRepository = FakeTraktActivityRepository()
    private val fakeAccountManager = FakeAccountManager()

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
        syncShowMetadataInteractor = syncShowMetadataInteractor,
        watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
        accountManager = fakeAccountManager,
        requestManagerRepository = requestManagerRepository,
        dispatchers = coroutineDispatcher,
        logger = fakeLogger,
    )

    fun create(
        componentContext: ComponentContext,
        navigator: Navigator = NoOpNavigator(),
    ): ContinueWatchingPresenter = ContinueWatchingPresenter(
        componentContext = componentContext,
        navigator = navigator,
        repository = repository,
        unfollowShowInteractor = UnfollowShowInteractor(
            followedShowsRepository = fakeFollowedShowsRepository,
            libraryRepository = FakeLibraryRepository(),
            appScopeLauncher = FakeAppScopeLauncher(scope = appCoroutineScope),
        ),
        observeWatchlistSectionsInteractor = observeWatchlistSectionsInteractor,
        observeUpNextSectionsInteractor = observeUpNextSectionsInteractor,
        markEpisodeWatchedInteractor = fakeMarkEpisodeWatchedInteractor,
        syncContinueWatchingInteractor = syncContinueWatchingInteractor,
        nitroFlag = nitroFlag,
        syncObserver = syncObserver,
        accountManager = fakeAccountManager,
        errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
        mapper = ContinueWatchingMapper(localizer),
        logger = fakeLogger,
    )
}
