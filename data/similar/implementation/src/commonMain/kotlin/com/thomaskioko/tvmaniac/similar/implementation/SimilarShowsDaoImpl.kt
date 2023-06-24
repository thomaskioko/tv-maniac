package com.thomaskioko.tvmaniac.similar.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.SimilarShows
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsDao
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class SimilarShowsDaoImpl(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : SimilarShowsDao {

    override fun insert(traktId: Long, similarShowId: Long) {
        database.similar_showsQueries.transaction {
            database.similar_showsQueries.insertOrReplace(
                id = similarShowId,
                trakt_id = traktId,
            )
        }
    }

    override fun observeSimilarShows(traktId: Long): Flow<List<SimilarShows>> {
        return database.similar_showsQueries.similarShows(trakt_id = traktId)
            .asFlow()
            .mapToList(dispatchers.io)
    }

    override fun delete(id: Long) {
        database.similar_showsQueries.delete(id)
    }

    override fun deleteAll() {
        database.transaction {
            database.similar_showsQueries.deleteAll()
        }
    }
}
