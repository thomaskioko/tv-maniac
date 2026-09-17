package com.thomaskioko.tvmaniac.genre

import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.genre.model.GenreShowCategory
import com.thomaskioko.tvmaniac.genre.model.GenreWithShowsEntity
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

public interface GenreRepository {
    public suspend fun saveGenreShowCategory(category: GenreShowCategory)

    public suspend fun getGenreShowCategory(): GenreShowCategory

    public fun observeGenreShowCategory(): Flow<GenreShowCategory>

    public suspend fun fetchGenresWithShows(
        forceRefresh: Boolean = false,
    )

    public suspend fun fetchShowByGenreId(
        id: String,
        forceRefresh: Boolean = false,
    )

    public fun observeGenresWithShows(): Flow<List<ShowGenresEntity>>

    public suspend fun observeShowByGenreId(
        id: String,
    ): Flow<List<Tvshow>>

    public suspend fun observeGenrePosters()

    public fun getGenreSlugs(): List<String>

    public suspend fun fetchTraktGenres(forceRefresh: Boolean = false)

    public suspend fun fetchGenreShows(
        slug: String,
        category: GenreShowCategory,
        forceRefresh: Boolean = false,
    )

    public fun observeGenresWithShowRows(): Flow<List<GenreWithShowsEntity>>

    public fun getPagedGenreShows(
        slug: String,
        category: GenreShowCategory,
        forceRefresh: Boolean = false,
    ): Flow<PagingData<ShowEntity>>
}
