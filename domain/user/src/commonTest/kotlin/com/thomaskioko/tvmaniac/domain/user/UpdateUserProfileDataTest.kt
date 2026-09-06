package com.thomaskioko.tvmaniac.domain.user

import com.thomaskioko.tvmaniac.accountmanager.testing.FakeProviderFeatures
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.lists.testing.FakeListRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class UpdateUserProfileDataTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var listRepository: FakeListRepository

    private fun buildInteractor(supportsLists: Boolean): UpdateUserProfileData {
        listRepository = FakeListRepository()
        return UpdateUserProfileData(
            userRepository = FakeUserRepository(),
            listRepository = listRepository,
            activeProviderFeatures = { FakeProviderFeatures(supportsLists = supportsLists) },
            dispatchers = dispatchers,
        )
    }

    @Test
    fun `should fetch user lists given trakt provider is active`() = runTest(testDispatcher) {
        val interactor = buildInteractor(supportsLists = true)

        interactor.executeSync(UpdateUserProfileData.Params(username = "me", forceRefresh = false))

        listRepository.fetchUserListsInvocations shouldBe 1
    }

    @Test
    fun `should skip user lists given simkl provider is active`() = runTest(testDispatcher) {
        val interactor = buildInteractor(supportsLists = false)

        interactor.executeSync(UpdateUserProfileData.Params(username = "me", forceRefresh = false))

        listRepository.fetchUserListsInvocations shouldBe 0
    }

    @Test
    fun `should skip user lists given no provider is active`() = runTest(testDispatcher) {
        val interactor = buildInteractor(supportsLists = false)

        interactor.executeSync(UpdateUserProfileData.Params(username = "me", forceRefresh = false))

        listRepository.fetchUserListsInvocations shouldBe 0
    }
}
