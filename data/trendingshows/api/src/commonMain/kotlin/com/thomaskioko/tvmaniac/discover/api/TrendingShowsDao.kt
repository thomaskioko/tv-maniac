package com.thomaskioko.tvmaniac.discover.api

import androidx.paging.PagingSource
import com.thomaskioko.tvmaniac.core.db.Trending_shows
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import kotlinx.coroutines.flow.Flow

interface TrendingShowsDao {
  fun upsert(show: Trending_shows)

  fun observeTvShow(): Flow<List<ShowEntity>>

  fun getPagedTrendingShows(): PagingSource<Int, ShowEntity>

  fun pageExists(page: Long): Boolean

  fun deleteTrendingShow(id: Long)

  fun deleteTrendingShows()
}
