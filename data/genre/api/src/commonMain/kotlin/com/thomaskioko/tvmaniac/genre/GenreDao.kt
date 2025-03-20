package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.db.Genres
import com.thomaskioko.tvmaniac.db.Tvshow
import kotlinx.coroutines.flow.Flow

interface GenreDao {
  fun upsert(entity: Genres)
  fun getGenres(): List<Genres>
  fun observeGenres(): Flow<List<ShowGenresEntity>>
  fun observeShowsByGenreId(id: String): Flow<List<Tvshow>>
}
