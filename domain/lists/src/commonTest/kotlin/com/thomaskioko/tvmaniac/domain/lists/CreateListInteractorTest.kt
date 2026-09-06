package com.thomaskioko.tvmaniac.domain.lists

import com.thomaskioko.tvmaniac.accountmanager.testing.FakeProviderFeatures
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.lists.testing.FakeListRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class CreateListInteractorTest {

    private val listRepository = FakeListRepository()
    private val userRepository = FakeUserRepository()

    private fun buildInteractor(supportsLists: Boolean): CreateListInteractor = CreateListInteractor(
        repository = listRepository,
        userRepository = userRepository,
        activeProviderFeatures = { FakeProviderFeatures(supportsLists = supportsLists) },
    )

    @Test
    fun `should throw given the name is blank`() = runTest {
        val interactor = buildInteractor(supportsLists = false)

        shouldThrow<IllegalArgumentException> {
            interactor.executeSync(CreateListInteractor.Params(name = "  "))
        }
    }

    @Test
    fun `should throw given the name exceeds 50 characters`() = runTest {
        val interactor = buildInteractor(supportsLists = false)

        shouldThrow<IllegalArgumentException> {
            interactor.executeSync(CreateListInteractor.Params(name = "a".repeat(51)))
        }
    }

    @Test
    fun `should create the list with no trakt slug given the active provider does not sync lists`() = runTest {
        val interactor = buildInteractor(supportsLists = false)

        interactor.executeSync(CreateListInteractor.Params(name = "Comfort watches"))

        listRepository.createdListNames() shouldBe listOf("Comfort watches")
        listRepository.lastCreateTraktSlug() shouldBe null
    }

    @Test
    fun `should create the list with the user slug given the active provider syncs lists`() = runTest {
        val interactor = buildInteractor(supportsLists = true)

        interactor.executeSync(CreateListInteractor.Params(name = "Comfort watches"))

        listRepository.lastCreateTraktSlug() shouldBe userRepository.getCurrentUser()?.slug
    }

    @Test
    fun `should create the list with no trakt slug given no user is signed in`() = runTest {
        userRepository.setUserProfile(null)
        val interactor = buildInteractor(supportsLists = true)

        interactor.executeSync(CreateListInteractor.Params(name = "Comfort watches"))

        listRepository.createdListNames() shouldBe listOf("Comfort watches")
        listRepository.lastCreateTraktSlug() shouldBe null
    }
}
