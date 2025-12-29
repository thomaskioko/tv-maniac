package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.db.Tvshow
import kotlinx.coroutines.flow.Flow

public interface GenreRepository {
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
}
