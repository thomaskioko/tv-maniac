package com.thomaskioko.tvmaniac.data.showdetails.implementation

import com.thomaskioko.tvmaniac.core.db.Genres
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import me.tatarka.inject.annotations.Inject

@Inject
class DefaultGenreDao(database: TvManiacDatabase) {
  private val networksQueries = database.genresQueries

  fun upsert(entity: Genres) {
    networksQueries.upsert(
      id = entity.id,
      tmdb_id = entity.tmdb_id,
      name = entity.name,
    )
  }
}
