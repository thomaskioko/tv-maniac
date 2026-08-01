package com.thomaskioko.tvmaniac.presentation.library

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.library.testing.FakeLibraryRepository
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.datastore.api.ListStyle
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.domain.library.ObserveLibraryInteractor
import com.thomaskioko.tvmaniac.domain.library.SyncLibraryInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ShowMetadataSyncHelper
import com.thomaskioko.tvmaniac.domain.showdetails.SyncShowMetadataInteractor
import com.thomaskioko.tvmaniac.domain.syncactivity.SyncActivityInteractor
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsRepository
import com.thomaskioko.tvmaniac.navigation.testing.FakeNavigator
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionFeature
import com.thomaskioko.tvmaniac.subscription.testing.FakeSubscriptionManager
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeActivitySyncRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class LibraryPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val libraryRepository = FakeLibraryRepository()
    private val subscriptionManager = FakeSubscriptionManager()
    private val accountManager = FakeAccountManager()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        lifecycle.resume()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit the stored layout given the premium layouts are unlocked`() = runTest {
        libraryRepository.setListStyle(ListStyle.COMPACT)
        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()
            state.listStyle shouldBe ListStyle.COMPACT
            state.isListStyleLocked shouldBe false
        }
    }

    @Test
    fun `should emit the free fallback given a premium layout is stored without access`() = runTest {
        libraryRepository.setListStyle(ListStyle.COMPACT)
        subscriptionManager.setAccess(SubscriptionFeature.ListViewTypes, false)
        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            val state = expectMostRecentItem()
            state.listStyle shouldBe ListStyle.LIST
            state.isListStyleLocked shouldBe true
        }
    }

    @Test
    fun `should emit grid given the detailed layout is stored without access`() = runTest {
        libraryRepository.setListStyle(ListStyle.DETAILED)
        subscriptionManager.setAccess(SubscriptionFeature.ListViewTypes, false)
        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem().listStyle shouldBe ListStyle.GRID
        }
    }

    @Test
    fun `should restore the stored layout given access returns`() = runTest {
        libraryRepository.setListStyle(ListStyle.DETAILED)
        subscriptionManager.setAccess(SubscriptionFeature.ListViewTypes, false)
        val presenter = createPresenter()

        presenter.state.test {
            testDispatcher.scheduler.advanceUntilIdle()
            expectMostRecentItem().listStyle shouldBe ListStyle.GRID

            subscriptionManager.setAccess(SubscriptionFeature.ListViewTypes, true)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            state.listStyle shouldBe ListStyle.DETAILED
            state.isListStyleLocked shouldBe false
        }
    }

    @Test
    fun `should save a free layout given the premium layouts are locked`() = runTest {
        subscriptionManager.setAccess(SubscriptionFeature.ListViewTypes, false)
        val presenter = createPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.dispatch(ChangeListStyleClicked(ListStyle.LIST))
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.state.test {
            expectMostRecentItem().listStyle shouldBe ListStyle.LIST
        }
    }

    @Test
    fun `should save a premium layout given the premium layouts are unlocked`() = runTest {
        val presenter = createPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.dispatch(ChangeListStyleClicked(ListStyle.COMPACT))
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.state.test {
            expectMostRecentItem().listStyle shouldBe ListStyle.COMPACT
        }
    }

    @Test
    fun `should keep the stored layout given a premium layout is selected while locked`() = runTest {
        libraryRepository.setListStyle(ListStyle.LIST)
        subscriptionManager.setAccess(SubscriptionFeature.ListViewTypes, false)
        val presenter = createPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.dispatch(ChangeListStyleClicked(ListStyle.DETAILED))
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.state.test {
            expectMostRecentItem().listStyle shouldBe ListStyle.LIST
        }
        libraryRepository.observeListStyle().test {
            awaitItem() shouldBe ListStyle.LIST
        }
    }

    @Test
    fun `should keep state unchanged given LibraryUpgradeClicked is dispatched`() = runTest {
        subscriptionManager.setAccess(SubscriptionFeature.ListViewTypes, false)
        val presenter = createPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.dispatch(LibraryUpgradeClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.state.test {
            val state = expectMostRecentItem()
            state.listStyle shouldBe ListStyle.GRID
            state.isListStyleLocked shouldBe true
        }
    }

    private fun createPresenter(): LibraryPresenter {
        val dispatchers = AppCoroutineDispatchers(
            main = testDispatcher,
            io = testDispatcher,
            computation = testDispatcher,
            databaseWrite = testDispatcher,
            databaseRead = testDispatcher,
        )
        val showDetailsRepository = FakeShowDetailsRepository()

        return LibraryPresenter(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            navigator = FakeNavigator(),
            repository = libraryRepository,
            observeLibraryInteractor = ObserveLibraryInteractor(repository = libraryRepository),
            syncLibraryInteractor = SyncLibraryInteractor(
                accountManager = accountManager,
                libraryRepository = libraryRepository,
                followedShowsRepository = FakeFollowedShowsRepository(),
                syncActivityInteractor = SyncActivityInteractor(
                    traktActivityRepository = FakeTraktActivityRepository(),
                    dispatchers = dispatchers,
                ),
                syncShowMetadataInteractor = SyncShowMetadataInteractor(
                    showDetailsRepository = showDetailsRepository,
                    seasonDetailsRepository = FakeSeasonDetailsRepository(),
                    watchProviderRepository = FakeWatchProviderRepository(),
                    dispatchers = dispatchers,
                ),
                showMetadataSyncHelper = ShowMetadataSyncHelper(FakeEpisodeRepository()),
                watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository(),
                syncRepository = FakeActivitySyncRepository(),
                datastoreRepository = FakeDatastoreRepository(),
                dateTimeProvider = FakeDateTimeProvider(),
                dispatchers = dispatchers,
                syncObserver = FakeSyncObserver(),
                logger = FakeLogger(),
            ),
            accountManager = accountManager,
            subscriptionManager = subscriptionManager,
            errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
            logger = FakeLogger(),
        )
    }
}
