package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.flow.Flow

public interface TvShowsDao {
    public fun upsert(show: ShowToPersist)

    public fun upsert(list: List<ShowToPersist>)

    public fun observeShowsByQuery(query: String): Flow<List<ShowEntity>>

    public fun observeQueryCount(query: String): Flow<Long>

    public suspend fun getQueryCount(query: String): Long

    public fun deleteTvShows()

    public fun upsertMerging(show: ShowToPersist)

    public fun getShowsByTraktIds(traktIds: List<Long>): List<ShowEntity>

    public fun getTmdbIdByTraktId(traktId: Long): Long?

    public suspend fun existsByTraktId(traktId: Long): Boolean
}
