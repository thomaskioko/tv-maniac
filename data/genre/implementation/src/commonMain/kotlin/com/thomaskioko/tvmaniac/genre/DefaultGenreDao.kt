package com.thomaskioko.tvmaniac.genre

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.db.Genres
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.db.Tvshows
import com.thomaskioko.tvmaniac.db.Id
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultGenreDao(
  private val database: TvManiacDatabase,
  private val dispatchers: AppCoroutineDispatchers,
) : GenreDao {
  private val genresQueries = database.genresQueries

  override fun upsert(entity: Genres) {
    genresQueries.upsert(
      id = entity.id,
      name = entity.name,
      poster_url = entity.poster_url
    )
  }

  override fun getGenres(): List<Genres> = genresQueries.genres().executeAsList()

  override fun observeGenres(): Flow<List<ShowGenresEntity>> {
    return genresQueries.genres()
    { id, name, posterUrl ->
      ShowGenresEntity(
        id = id.id,
        name = name,
        posterUrl = posterUrl,
      )
    }
      .asFlow()
      .mapToList(dispatchers.io)
  }

  override fun observeShowsByGenreId(id: String): Flow<List<Tvshows>> {
    return database.show_genresQueries.showsByGenreId(Id(id.toLong()))
      .asFlow()
      .mapToList(dispatchers.io)
  }
}