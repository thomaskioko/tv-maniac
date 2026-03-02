package com.thomaskioko.tvmaniac.domain.genre

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.genre.GenreRepository
import com.thomaskioko.tvmaniac.genre.model.GenreShowCategory
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
public class FetchGenreContentInteractor(
    private val repository: GenreRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<FetchGenreContentInteractor.Params>() {

    override suspend fun doWork(params: Params) {
        withContext(dispatchers.io) {
            repository.fetchTraktGenres(params.forceRefresh)
            repository.getGenreSlugs()
                .filter { it in CURATED_GENRE_SLUGS }
                .forEach { slug ->
                    repository.fetchGenreShows(
                        slug = slug,
                        category = params.category,
                        forceRefresh = params.forceRefresh,
                    )
                }
        }
    }

    public data class Params(
        val category: GenreShowCategory,
        val forceRefresh: Boolean = false,
    )

    internal companion object {
        val CURATED_GENRE_SLUGS = setOf(
            "action",
            "comedy",
            "drama",
            "fantasy",
            "horror",
            "science-fiction",
            "thriller",
            "animation",
            "mystery",
            "romance",
        )
    }
}
