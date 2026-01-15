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

    public fun getShowByTraktId(traktId: Long): Tvshow?

    public fun showExistsByTraktId(traktId: Long): Boolean

    public fun getShowsByTraktIds(traktIds: List<Long>): List<ShowEntity>

    public fun getTmdbIdByTraktId(traktId: Long): Long?
}
