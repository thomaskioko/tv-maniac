package com.thomaskioko.tvmaniac.domain.favorites

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.favorites.api.FavoriteShow
import com.thomaskioko.tvmaniac.favorites.testing.FakeFavoritesRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class ObserveFavoritesInteractorTest {

    private val repository = FakeFavoritesRepository()
    private val interactor = ObserveFavoritesInteractor(repository)

    @Test
    fun `should emit favorite shows when repository updates`() = runTest {
        interactor(Unit)

        interactor.flow.test {
            awaitItem().shouldBeEmpty()

            repository.setFavorites(shows)

            awaitItem() shouldContainExactly shows
            cancelAndIgnoreRemainingEvents()
        }
    }

    private companion object {
        val shows = listOf(
            FavoriteShow(
                showId = 1,
                tmdbId = 1,
                title = "Breaking Bad",
                posterPath = "/1.jpg",
                year = "2008",
            ),
            FavoriteShow(
                showId = 2,
                tmdbId = 2,
                title = "Severance",
                posterPath = "/2.jpg",
                year = "2022",
            ),
        )
    }
}
