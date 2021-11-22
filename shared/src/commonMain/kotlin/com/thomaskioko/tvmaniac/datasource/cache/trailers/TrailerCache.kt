package com.thomaskioko.tvmaniac.datasource.cache.trailers

import com.thomaskioko.tvmaniac.datasource.cache.SelectByShowId
import com.thomaskioko.tvmaniac.datasource.cache.Trailers

interface TrailerCache {

    fun insert(trailer: Trailers)

    fun insert(trailers: List<Trailers>)

    fun getTrailers(showId: Int): List<SelectByShowId>
}
