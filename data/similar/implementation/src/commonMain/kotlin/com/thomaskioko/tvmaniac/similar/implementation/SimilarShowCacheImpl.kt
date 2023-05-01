package com.thomaskioko.tvmaniac.similar.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.SelectSimilarShows
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.similar.api.SimilarShowCache
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class SimilarShowCacheImpl(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : SimilarShowCache {

    override fun insert(traktId: Long, similarShowId: Long) {
        database.similar_showsQueries.transaction {
            database.similar_showsQueries.insertOrReplace(
                id = similarShowId,
                trakt_id = traktId,
            )
        }
    }

    override fun observeSimilarShows(traktId: Long): Flow<List<SelectSimilarShows>> {
        return database.similar_showsQueries.selectSimilarShows(trakt_id = traktId)
            .asFlow()
            .mapToList(dispatchers.io)
    }
}
