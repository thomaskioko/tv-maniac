package com.thomaskioko.tvmaniac.domain.trailers.implementation

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.TrailerCache
import kotlinx.coroutines.flow.Flow

class TrailerCacheImpl(
    private val database: TvManiacDatabase
): TrailerCache {

    override fun insert(trailer: Trailers) {
        database.trailersQueries.insertOrReplace(
            id = trailer.id,
            trakt_id = trailer.trakt_id,
            key = trailer.key,
            name = trailer.name,
            site = trailer.site,
            size = trailer.size,
            type = trailer.type
        )
    }

    override fun insert(trailerList: List<Trailers>) {
        trailerList.forEach { insert(it) }
    }

    override fun getTrailersByShowId(showId: Int): Flow<List<Trailers>> {
        return database.trailersQueries.selectByShowId(showId)
            .asFlow()
            .mapToList()
    }
}