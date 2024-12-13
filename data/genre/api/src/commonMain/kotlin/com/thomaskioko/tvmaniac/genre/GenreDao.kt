package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.core.db.Genres
import kotlinx.coroutines.flow.Flow

interface GenreDao {
  fun upsert(entity: Genres)
  fun getGenres(): List<Genres>
  fun observeGenresWithShows(): Flow<List<ShowGenresEntity>>
}
