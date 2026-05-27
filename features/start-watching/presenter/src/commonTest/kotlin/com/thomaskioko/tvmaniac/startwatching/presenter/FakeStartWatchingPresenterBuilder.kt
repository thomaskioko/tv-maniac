package com.thomaskioko.tvmaniac.startwatching.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.domain.startwatching.ObserveStartWatchingInteractor
import com.thomaskioko.tvmaniac.domain.startwatching.SyncStartWatchingInteractor
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.testing.NoOpNavigator
import com.thomaskioko.tvmaniac.startwatching.testing.FakeStartWatchingRepository
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
import com.thomaskioko.tvmaniac.watchlistprefs.testing.FakeWatchlistPrefsRepository
import kotlinx.coroutines.test.UnconfinedTestDispatcher

class FakeStartWatchingPresenterBuilder {
    val startWatchingRepository = FakeStartWatchingRepository()
    val prefsRepository = FakeWatchlistPrefsRepository()
    val syncObserver = FakeSyncObserver()

    val testDispatcher = UnconfinedTestDispatcher()

    private val fakeLogger = FakeLogger()

    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val syncStartWatchingInteractor = SyncStartWatchingInteractor(
        startWatchingRepository = startWatchingRepository,
        watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository(),
        dispatchers = coroutineDispatcher,
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
        syncStartWatchingInteractor = syncStartWatchingInteractor,
        errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
        logger = fakeLogger,
    )
}
