package com.thomaskioko.tvmaniac.datasource.cache.trailers

import com.thomaskioko.tvmaniac.datasource.cache.SelectByShowId
import com.thomaskioko.tvmaniac.datasource.cache.Trailers
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase

class TrailerCacheImpl(
    private val database: TvManiacDatabase
) : TrailerCache {

    private val trailersQueries get() = database.trailersQueries

    override fun insert(trailer: Trailers) {
        trailersQueries.insertOrReplace(
            id = trailer.id,
            show_id = trailer.show_id,
            key = trailer.key,
            name = trailer.name,
            site = trailer.site,
            size = trailer.size,
            type = trailer.type
        )
    }

    override fun insert(trailers: List<Trailers>) {
        trailers.forEach { insert(it) }
    }

    override fun getTrailers(showId: Int): List<SelectByShowId> {
      return trailersQueries.selectByShowId(showId.toLong())
          .executeAsList()
    }
}