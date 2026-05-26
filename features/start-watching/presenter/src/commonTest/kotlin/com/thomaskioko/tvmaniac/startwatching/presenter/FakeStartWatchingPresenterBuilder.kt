package com.thomaskioko.tvmaniac.startwatching.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.startwatching.ObserveStartWatchingInteractor
import com.thomaskioko.tvmaniac.domain.startwatching.SyncStartWatchingFirstSeasonInteractor
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.testing.NoOpNavigator
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsEpisodesSyncRepository
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import com.thomaskioko.tvmaniac.startwatching.testing.FakeStartWatchingRepository
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
import com.thomaskioko.tvmaniac.watchlistprefs.testing.FakeWatchlistPrefsRepository
import kotlinx.coroutines.test.UnconfinedTestDispatcher

class FakeStartWatchingPresenterBuilder {
    val startWatchingRepository = FakeStartWatchingRepository()
    val prefsRepository = FakeWatchlistPrefsRepository()
    val syncObserver = FakeSyncObserver()
    val episodeRepository = FakeEpisodeRepository()
    val seasonsRepository = FakeSeasonsRepository()
    val seasonsEpisodesSyncRepository = FakeSeasonsEpisodesSyncRepository()
    val seasonDetailsRepository = FakeSeasonDetailsRepository()
    val requestManagerRepository = FakeRequestManagerRepository(initialRequestValid = false)

    val testDispatcher = UnconfinedTestDispatcher()

    private val fakeLogger = FakeLogger()

    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val markEpisodeWatchedInteractor = MarkEpisodeWatchedInteractor(
        episodeRepository = episodeRepository,
    )

    private val syncStartWatchingFirstSeasonInteractor = SyncStartWatchingFirstSeasonInteractor(
        startWatchingRepository = startWatchingRepository,
        seasonsEpisodesSyncRepository = seasonsEpisodesSyncRepository,
        seasonsRepository = seasonsRepository,
        seasonDetailsRepository = seasonDetailsRepository,
        requestManagerRepository = requestManagerRepository,
        syncObserver = syncObserver,
        dispatchers = coroutineDispatcher,
        logger = fakeLogger,
    )

    fun create(
        componentContext: ComponentContext,
        navigator: Navigator = NoOpNavigator(),
    ): StartWatchingPresenter = StartWatchingPresenter(
        componentContext = componentContext,
        repository = prefsRepository,
        observeStartWatchingInteractor = ObserveStartWatchingInteractor(repository = startWatchingRepository),
        syncObserver = syncObserver,
        navigator = navigator,
        markEpisodeWatchedInteractor = markEpisodeWatchedInteractor,
        syncStartWatchingFirstSeasonInteractor = syncStartWatchingFirstSeasonInteractor,
        errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
        logger = fakeLogger,
    )
}
