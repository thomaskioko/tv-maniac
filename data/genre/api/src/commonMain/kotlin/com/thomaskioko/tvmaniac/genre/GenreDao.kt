package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.db.Genres
import com.thomaskioko.tvmaniac.db.Tvshow
import kotlinx.coroutines.flow.Flow

public interface GenreDao {
    public fun upsert(entity: Genres)
    public fun getGenres(): List<Genres>
    public fun getGenre(id: Long): Genres
    public fun observeGenres(): Flow<List<ShowGenresEntity>>
    public fun observeShowsByGenreId(id: String): Flow<List<Tvshow>>
}
