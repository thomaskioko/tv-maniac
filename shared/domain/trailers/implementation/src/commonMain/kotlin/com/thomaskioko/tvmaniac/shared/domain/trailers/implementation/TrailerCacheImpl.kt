package com.thomaskioko.tvmaniac.shared.domain.trailers.implementation

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.thomaskioko.tvmaniac.datasource.cache.Trailers
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.TrailerCache
import kotlinx.coroutines.flow.Flow

class TrailerCacheImpl(
    private val database: TvManiacDatabase
): TrailerCache {

    override fun insert(trailer: Trailers) {
        database.trailersQueries.insertOrReplace(
            id = trailer.id,
            show_id = trailer.show_id,
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

    override fun getTrailersByShowId(showId: Long): Flow<List<Trailers>> {
        return database.trailersQueries.selectByShowId(showId)
            .asFlow()
            .mapToList()
    }
}