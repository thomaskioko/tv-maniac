package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.db.Tvshows
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

interface TvShowsDao {
  fun upsert(show: Tvshows)

  fun upsert(list: List<Tvshows>)

  fun observeShowsByQuery(query: String): Flow<List<ShowEntity>>

  fun observeQueryCount(query: String): Flow<Long>

  fun deleteTvShows()
}
