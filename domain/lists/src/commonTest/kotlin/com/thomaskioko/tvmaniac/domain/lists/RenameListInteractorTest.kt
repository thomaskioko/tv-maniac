package com.thomaskioko.tvmaniac.domain.lists

import com.thomaskioko.tvmaniac.accountmanager.testing.FakeProviderFeatures
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.lists.testing.FakeListRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class RenameListInteractorTest {

    private val listRepository = FakeListRepository()
    private val userRepository = FakeUserRepository()

    private fun buildInteractor(supportsLists: Boolean): RenameListInteractor = RenameListInteractor(
        repository = listRepository,
        userRepository = userRepository,
        activeProviderFeatures = { FakeProviderFeatures(supportsLists = supportsLists) },
    )

    @Test
    fun `should throw given the name is blank`() = runTest {
        val interactor = buildInteractor(supportsLists = false)

        shouldThrow<IllegalArgumentException> {
            interactor.executeSync(RenameListInteractor.Params(listId = 1L, name = "  "))
        }
    }

    @Test
    fun `should throw given the name exceeds 50 characters`() = runTest {
        val interactor = buildInteractor(supportsLists = false)

        shouldThrow<IllegalArgumentException> {
            interactor.executeSync(RenameListInteractor.Params(listId = 1L, name = "a".repeat(51)))
        }
    }

    @Test
    fun `should rename the list with no trakt slug given the active provider does not sync lists`() = runTest {
        val interactor = buildInteractor(supportsLists = false)

        interactor.executeSync(RenameListInteractor.Params(listId = 1L, name = "Comfort watches"))

        listRepository.renamedLists() shouldBe listOf(1L to "Comfort watches")
        listRepository.lastRenameTraktSlug() shouldBe null
    }

    @Test
    fun `should rename the list with the user slug given the active provider syncs lists`() = runTest {
        val interactor = buildInteractor(supportsLists = true)

        interactor.executeSync(RenameListInteractor.Params(listId = 1L, name = "Comfort watches"))

        listRepository.lastRenameTraktSlug() shouldBe userRepository.getCurrentUser()?.slug
    }

    @Test
    fun `should rename the list with no trakt slug given no user is signed in`() = runTest {
        userRepository.setUserProfile(null)
        val interactor = buildInteractor(supportsLists = true)

        interactor.executeSync(RenameListInteractor.Params(listId = 1L, name = "Comfort watches"))

        listRepository.renamedLists() shouldBe listOf(1L to "Comfort watches")
        listRepository.lastRenameTraktSlug() shouldBe null
    }

    @Test
    fun `should trim the name given it has surrounding whitespace`() = runTest {
        val interactor = buildInteractor(supportsLists = false)

        interactor.executeSync(RenameListInteractor.Params(listId = 1L, name = "  Comfort watches  "))

        listRepository.renamedLists() shouldBe listOf(1L to "Comfort watches")
    }
}
