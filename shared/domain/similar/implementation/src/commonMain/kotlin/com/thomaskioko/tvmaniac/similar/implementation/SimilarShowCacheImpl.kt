package com.thomaskioko.tvmaniac.similar.implementation

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.SelectSimilarShows
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.similar.api.SimilarShowCache
import kotlinx.coroutines.flow.Flow

class SimilarShowCacheImpl(
    private val database: TvManiacDatabase
) : SimilarShowCache {

    override fun insert(traktId: Int, similarShowId: Int) {
        database.similarShowQueries.transaction {
            database.similarShowQueries.insertOrReplace(
                id = similarShowId,
                trakt_id = traktId
            )
        }
    }

    override fun observeSimilarShows(traktId: Int): Flow<List<SelectSimilarShows>> {
        return database.similarShowQueries.selectSimilarShows(trakt_id = traktId)
            .asFlow()
            .mapToList()
    }
}
