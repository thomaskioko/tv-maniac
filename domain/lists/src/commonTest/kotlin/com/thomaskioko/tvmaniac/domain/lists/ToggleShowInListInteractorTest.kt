package com.thomaskioko.tvmaniac.domain.lists

import com.thomaskioko.tvmaniac.accountmanager.testing.FakeProviderFeatures
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.lists.testing.FakeListRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class ToggleShowInListInteractorTest {

    private val listRepository = FakeListRepository()
    private val userRepository = FakeUserRepository()

    private fun buildInteractor(supportsLists: Boolean): ToggleShowInListInteractor = ToggleShowInListInteractor(
        repository = listRepository,
        userRepository = userRepository,
        activeProviderFeatures = { FakeProviderFeatures(supportsLists = supportsLists) },
    )

    @Test
    fun `should toggle with no trakt slug given the active provider does not sync lists`() = runTest {
        val interactor = buildInteractor(supportsLists = false)

        interactor.executeSync(ToggleShowInListInteractor.Params(listId = 1L, showId = 100L, isCurrentlyInList = false))

        listRepository.toggledShows() shouldBe listOf(1L to 100L)
        listRepository.lastToggleTraktSlug() shouldBe null
    }

    @Test
    fun `should toggle with the user slug given the active provider syncs lists`() = runTest {
        val interactor = buildInteractor(supportsLists = true)

        interactor.executeSync(ToggleShowInListInteractor.Params(listId = 1L, showId = 100L, isCurrentlyInList = false))

        listRepository.lastToggleTraktSlug() shouldBe userRepository.getCurrentUser()?.slug
    }

    @Test
    fun `should toggle with no trakt slug given no user is signed in`() = runTest {
        userRepository.setUserProfile(null)
        val interactor = buildInteractor(supportsLists = true)

        interactor.executeSync(ToggleShowInListInteractor.Params(listId = 1L, showId = 100L, isCurrentlyInList = false))

        listRepository.toggledShows() shouldBe listOf(1L to 100L)
        listRepository.lastToggleTraktSlug() shouldBe null
    }
}
