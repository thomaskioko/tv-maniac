package com.thomaskioko.tvmaniac.domain.user

import com.thomaskioko.tvmaniac.accountmanager.testing.FakeProviderFeatures
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.traktlists.testing.FakeTraktListRepository
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

    private lateinit var traktListRepository: FakeTraktListRepository

    private fun buildInteractor(supportsLists: Boolean): UpdateUserProfileData {
        traktListRepository = FakeTraktListRepository()
        return UpdateUserProfileData(
            userRepository = FakeUserRepository(),
            traktListRepository = traktListRepository,
            activeProviderFeatures = { FakeProviderFeatures(supportsLists = supportsLists) },
            dispatchers = dispatchers,
        )
    }

    @Test
    fun `should fetch user lists given trakt provider is active`() = runTest(testDispatcher) {
        val interactor = buildInteractor(supportsLists = true)

        interactor.executeSync(UpdateUserProfileData.Params(username = "me", forceRefresh = false))

        traktListRepository.fetchUserListsInvocations shouldBe 1
    }

    @Test
    fun `should skip user lists given simkl provider is active`() = runTest(testDispatcher) {
        val interactor = buildInteractor(supportsLists = false)

        interactor.executeSync(UpdateUserProfileData.Params(username = "me", forceRefresh = false))

        traktListRepository.fetchUserListsInvocations shouldBe 0
    }

    @Test
    fun `should skip user lists given no provider is active`() = runTest(testDispatcher) {
        val interactor = buildInteractor(supportsLists = false)

        interactor.executeSync(UpdateUserProfileData.Params(username = "me", forceRefresh = false))

        traktListRepository.fetchUserListsInvocations shouldBe 0
    }
}
