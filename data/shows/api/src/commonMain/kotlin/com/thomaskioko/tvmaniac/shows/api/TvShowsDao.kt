package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.db.Provider
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity

public interface TvShowsDao {
    public fun upsert(show: ShowToPersist)

    public fun upsert(list: List<ShowToPersist>)

    public fun upsertExternalId(tmdbId: Long, provider: Provider, externalId: String)

    public fun getTmdbIdsWithPoster(tmdbIds: List<Long>): Set<Long>

    public fun deleteTvShows()

    public fun upsertMerging(show: ShowToPersist)

    public fun getShowsByIds(showIds: List<Long>): List<ShowEntity>

    public fun getTmdbIdByShowId(showId: Long): Long?

    public fun getTmdbIdForLocalShowId(showId: Long): Long?

    public fun getLocalShowIdByTmdbId(tmdbId: Long): Long?

    public fun getTraktIdByTmdbId(tmdbId: Long): Long?

    public suspend fun existsByShowId(showId: Long): Boolean
}
