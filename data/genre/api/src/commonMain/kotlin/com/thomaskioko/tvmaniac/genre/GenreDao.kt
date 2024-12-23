package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.core.db.Genres
import com.thomaskioko.tvmaniac.core.db.Tvshows
import kotlinx.coroutines.flow.Flow

interface GenreDao {
  fun upsert(entity: Genres)
  fun getGenres(): List<Genres>
  fun observeGenres(): Flow<List<ShowGenresEntity>>
  fun observeShowsByGenreId(id: String): Flow<List<Tvshows>>
}
