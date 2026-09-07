package com.thomaskioko.tvmaniac.domain.lists

import com.thomaskioko.tvmaniac.accountmanager.testing.FakeProviderFeatures
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.lists.testing.FakeListRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class DeleteListInteractorTest {

    private val listRepository = FakeListRepository()
    private val userRepository = FakeUserRepository()

    private fun buildInteractor(supportsLists: Boolean): DeleteListInteractor = DeleteListInteractor(
        repository = listRepository,
        userRepository = userRepository,
        activeProviderFeatures = { FakeProviderFeatures(supportsLists = supportsLists) },
    )

    @Test
    fun `should delete the list with no trakt slug given the active provider does not sync lists`() = runTest {
        val interactor = buildInteractor(supportsLists = false)

        interactor.executeSync(DeleteListInteractor.Params(listId = 1L))

        listRepository.deletedListIds() shouldBe listOf(1L)
        listRepository.lastDeleteTraktSlug() shouldBe null
    }

    @Test
    fun `should delete the list with the user slug given the active provider syncs lists`() = runTest {
        val interactor = buildInteractor(supportsLists = true)

        interactor.executeSync(DeleteListInteractor.Params(listId = 1L))

        listRepository.lastDeleteTraktSlug() shouldBe userRepository.getCurrentUser()?.slug
    }

    @Test
    fun `should delete the list with no trakt slug given no user is signed in`() = runTest {
        userRepository.setUserProfile(null)
        val interactor = buildInteractor(supportsLists = true)

        interactor.executeSync(DeleteListInteractor.Params(listId = 1L))

        listRepository.deletedListIds() shouldBe listOf(1L)
        listRepository.lastDeleteTraktSlug() shouldBe null
    }
}
