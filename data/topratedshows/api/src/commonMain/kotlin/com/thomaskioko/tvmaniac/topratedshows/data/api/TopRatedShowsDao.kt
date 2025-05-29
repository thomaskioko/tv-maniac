package com.thomaskioko.tvmaniac.topratedshows.data.api

import androidx.paging.PagingSource
import com.thomaskioko.tvmaniac.db.TopRatedShows
import com.thomaskioko.tvmaniac.db.Toprated_shows
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

interface TopRatedShowsDao {
    fun upsert(show: Toprated_shows)

    fun observeTopRatedShows(): Flow<List<TopRatedShows>>

    fun getPagedTopRatedShows(): PagingSource<Int, ShowEntity>

    fun observeTopRatedShows(page: Long): Flow<List<ShowEntity>>

    fun pageExists(page: Long): Boolean

    fun deleteTrendingShows(id: Long)

    fun deleteTrendingShows()
}
