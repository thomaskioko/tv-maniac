package com.thomaskioko.tvmaniac.similar.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.SelectSimilarShows
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.similar.api.SimilarShowCache
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

class SimilarShowCacheImpl(
    private val database: TvManiacDatabase,
    private val coroutineContext: CoroutineContext
) : SimilarShowCache {

    override fun insert(traktId: Long, similarShowId: Long) {
        database.similarShowQueries.transaction {
            database.similarShowQueries.insertOrReplace(
                id = similarShowId,
                trakt_id = traktId
            )
        }
    }

    override fun observeSimilarShows(traktId: Long): Flow<List<SelectSimilarShows>> {
        return database.similarShowQueries.selectSimilarShows(trakt_id = traktId)
            .asFlow()
            .mapToList(coroutineContext)
    }
}
