package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

public interface TvShowsDao {
    public fun upsert(show: Tvshow)

    public fun upsert(list: List<Tvshow>)

    public fun observeShowsByQuery(query: String): Flow<List<ShowEntity>>

    public fun observeQueryCount(query: String): Flow<Long>

    public fun deleteTvShows()

    public suspend fun shouldUpdateShows(shows: List<Int>): Boolean

    public fun getShowById(id: Long): Tvshow?

    public fun showExists(id: Long): Boolean

    public fun getShowsByIds(ids: List<Long>): List<ShowEntity>
}
