package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.db.Tvshows
import kotlinx.coroutines.flow.Flow

interface TvShowsDao {
  fun upsert(show: Tvshows)

  fun upsert(list: List<Tvshows>)

  fun observeShowsByQuery(query: String): Flow<List<ShowEntity>>

  fun deleteTvShows()
}
