package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeProviderFeatures
import com.thomaskioko.tvmaniac.core.base.interactor.executeSync
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.library.testing.FakeLibraryRepository
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.lists.testing.FakeListRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class SyncPendingUploadsInteractorTest {

    private val syncRepository = FakeWatchedEpisodeSyncRepository()
    private val libraryRepository = FakeLibraryRepository()
    private val listRepository = FakeListRepository()
    private val userRepository = FakeUserRepository()
    private val accountManager = FakeAccountManager()
    private val logger = FakeLogger()
    private var providerFeatures = FakeProviderFeatures(supportsLists = true)

    private val interactor = SyncPendingUploadsInteractor(
        syncRepository = syncRepository,
        libraryRepository = libraryRepository,
        listRepository = listRepository,
        userRepository = userRepository,
        activeProviderFeatures = { providerFeatures },
        accountManager = accountManager,
        logger = logger,
    )

    @Test
    fun `should push all three queues given an active account`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        providerFeatures = FakeProviderFeatures(supportsLists = true)

        interactor.executeSync()

        syncRepository.syncPendingCallCount() shouldBe 1
        libraryRepository.syncPendingFollowedShowsInvocations() shouldBe 1
        listRepository.syncPendingListsCalls() shouldBe listOf("test-user")
    }

    @Test
    fun `should skip the list push given the provider has no list support`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.SIMKL)
        providerFeatures = FakeProviderFeatures(supportsLists = false)

        interactor.executeSync()

        syncRepository.syncPendingCallCount() shouldBe 1
        libraryRepository.syncPendingFollowedShowsInvocations() shouldBe 1
        listRepository.syncPendingListsCalls().shouldBeEmpty()
    }

    @Test
    fun `should skip the list push given no current user slug`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        providerFeatures = FakeProviderFeatures(supportsLists = true)
        userRepository.setUserProfile(null)

        interactor.executeSync()

        listRepository.syncPendingListsCalls().shouldBeEmpty()
    }

    @Test
    fun `should make no request given no active account`() = runTest {
        accountManager.setActiveProvider(null)
        syncRepository.setPendingEpisodesError(RuntimeException("should never run"))

        interactor.executeSync()

        syncRepository.syncPendingCallCount() shouldBe 0
        libraryRepository.syncPendingFollowedShowsInvocations() shouldBe 0
        listRepository.syncPendingListsCalls().shouldBeEmpty()
    }
}
