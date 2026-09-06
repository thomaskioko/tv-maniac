package com.thomaskioko.tvmaniac.domain.lists

import com.thomaskioko.tvmaniac.accountmanager.testing.FakeProviderFeatures
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.lists.testing.FakeListRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class SyncListsInteractorTest {

    private val listRepository = FakeListRepository()
    private val userRepository = FakeUserRepository()

    private fun buildInteractor(supportsLists: Boolean): SyncListsInteractor = SyncListsInteractor(
        repository = listRepository,
        userRepository = userRepository,
        activeProviderFeatures = { FakeProviderFeatures(supportsLists = supportsLists) },
    )

    @Test
    fun `should push pending lists before fetching user lists given the active provider syncs lists`() = runTest {
        val interactor = buildInteractor(supportsLists = true)

        interactor.executeSync(SyncListsInteractor.Params(forceRefresh = true))

        listRepository.callOrder() shouldBe listOf("syncPendingLists", "fetchUserLists")
    }

    @Test
    fun `should skip syncing given the active provider does not sync lists`() = runTest {
        val interactor = buildInteractor(supportsLists = false)

        interactor.executeSync(SyncListsInteractor.Params(forceRefresh = true))

        listRepository.callOrder().shouldBeEmpty()
    }

    @Test
    fun `should skip syncing given no user is signed in`() = runTest {
        userRepository.setUserProfile(null)
        val interactor = buildInteractor(supportsLists = true)

        interactor.executeSync(SyncListsInteractor.Params(forceRefresh = true))

        listRepository.callOrder().shouldBeEmpty()
    }
}
