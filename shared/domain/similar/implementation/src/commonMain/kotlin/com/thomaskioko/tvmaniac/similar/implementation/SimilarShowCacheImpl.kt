package com.thomaskioko.tvmaniac.similar.implementation

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.thomaskioko.tvmaniac.datasource.cache.SelectSimilarShows
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.similar.api.SimilarShowCache
import kotlinx.coroutines.flow.Flow

class SimilarShowCacheImpl(
    private val database: TvManiacDatabase
) : SimilarShowCache {

    override fun insert(showId: Long, similarShowId: Long) {
        database.showQueries.transaction {
            database.similarShowQueries.insertOrReplace(
                id = similarShowId,
                show_id = showId
            )
        }
    }

    override fun observeSimilarShows(showId: Long): Flow<List<SelectSimilarShows>> {
        return database.similarShowQueries.selectSimilarShows(show_id = showId)
            .asFlow()
            .mapToList()
    }
}
