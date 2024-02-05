package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.db.Tvshows
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import me.tatarka.inject.annotations.Inject

@Inject
class DefaultTvShowsDao(
  database: TvManiacDatabase,
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

  override fun deleteTvShows() {
    tvShowsQueries.transaction { tvShowsQueries.deleteAll() }
  }
}
