package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

interface TvShowsDao {
    fun upsert(show: Tvshow)

    fun upsert(list: List<Tvshow>)

    fun observeShowsByQuery(query: String): Flow<List<ShowEntity>>

    fun observeQueryCount(query: String): Flow<Long>

    fun deleteTvShows()

    suspend fun shouldUpdateShows(shows: List<Int>): Boolean

    fun getShowById(id: Long): Tvshow?

    fun showExists(id: Long): Boolean

    fun getShowsByIds(ids: List<Long>): List<ShowEntity>
}
