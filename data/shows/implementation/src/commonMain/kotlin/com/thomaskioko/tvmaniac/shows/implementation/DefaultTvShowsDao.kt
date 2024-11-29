package com.thomaskioko.tvmaniac.shows.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.db.Tvshows
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class)
class DefaultTvShowsDao(
  database: TvManiacDatabase,
  private val dispatchers: AppCoroutineDispatchers,
) : TvShowsDao {

  private val tvShowsQueries = database.tvshowsQueries

  override fun upsert(show: Tvshows) {
    tvShowsQueries.transaction {
      tvShowsQueries.upsert(
        id = show.id,
        name = show.name,
        overview = show.overview,
        language = show.language,
        first_air_date = show.first_air_date,
        vote_average = show.vote_average,
        vote_count = show.vote_count,
        popularity = show.popularity,
        genre_ids = show.genre_ids,
        status = show.status,
        episode_numbers = show.episode_numbers,
        last_air_date = show.last_air_date,
        season_numbers = show.season_numbers,
        poster_path = show.poster_path,
        backdrop_path = show.backdrop_path,
      )
    }
  }

  override fun upsert(list: List<Tvshows>) {
    list.forEach { upsert(it) }
  }

  override fun observeShowsByQuery(query: String): Flow<List<ShowEntity>> {
    return tvShowsQueries
      .searchShows(query, query, query, query)
      { id, title, imageUrl, overview, status, voteAverage, year, inLibrary ->
        ShowEntity(
          id = id.id,
          title = title,
          posterPath = imageUrl,
          inLibrary = inLibrary == 1L,
          overview = overview,
          status = status,
          voteAverage = voteAverage,
          year = year
        )
      }
      .asFlow()
      .mapToList(dispatchers.io)
  }

  override fun observeQueryCount(query: String): Flow<Long> {
    return tvShowsQueries.searchShowsCount(query, query)
      .asFlow()
      .mapToOne(dispatchers.io)
  }

  override fun deleteTvShows() {
    tvShowsQueries.transaction { tvShowsQueries.deleteAll() }
  }
}
