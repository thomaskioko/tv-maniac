package com.thomaskioko.tvmaniac.genre

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.db.Genres
import com.thomaskioko.tvmaniac.core.db.Show_genres
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultGenreDao(
  database: TvManiacDatabase,
  private val dispatchers: AppCoroutineDispatchers,
) : GenreDao {
  private val genresQueries = database.genresQueries
  private val showGenresQueries = database.show_genresQueries

  override fun upsert(entity: Genres) {
    genresQueries.upsert(
      id = entity.id,
      name = entity.name,
    )
  }

  override fun getGenres(): List<Genres> = genresQueries.genres().executeAsList()

  override fun observeGenresWithShows(): Flow<List<ShowGenresEntity>> {
    return showGenresQueries.showGenre()
    { id, name, posterUrl, count ->
      ShowGenresEntity(
        id = id.id,
        name = name,
        posterUrl = posterUrl,
        resultCount = count
      )
    }
      .asFlow()
      .mapToList(dispatchers.io)
  }
}
