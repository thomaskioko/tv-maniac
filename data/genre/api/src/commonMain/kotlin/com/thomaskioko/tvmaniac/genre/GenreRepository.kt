package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.db.Tvshow
import kotlinx.coroutines.flow.Flow

interface GenreRepository {
    suspend fun fetchGenresWithShows(
        forceRefresh: Boolean = false,
    )

    suspend fun fetchShowByGenreId(
        id: String,
        forceRefresh: Boolean = false,
    )

    fun observeGenresWithShows(): Flow<List<ShowGenresEntity>>

    suspend fun observeShowByGenreId(
        id: String,
    ): Flow<List<Tvshow>>
}
