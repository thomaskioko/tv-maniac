package com.thomaskioko.tvmaniac.data.trailers.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class TrailerDaoImpl(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : TrailerDao {

    override fun insert(trailer: Trailers) {
        database.trailersQueries.insertOrReplace(
            id = trailer.id,
            trakt_id = trailer.trakt_id,
            key = trailer.key,
            name = trailer.name,
            site = trailer.site,
            size = trailer.size,
            type = trailer.type,
        )
    }

    override fun insert(trailerList: List<Trailers>) {
        trailerList.forEach { insert(it) }
    }

    override fun observeTrailersById(showId: Long): Flow<List<Trailers>> {
        return database.trailersQueries.selectByShowId(showId)
            .asFlow()
            .mapToList(dispatchers.io)
    }
}
