package com.thomaskioko.tvmaniac.shared.domain.trailers.api

import com.thomaskioko.tvmaniac.datasource.cache.SelectByShowId
import com.thomaskioko.tvmaniac.datasource.cache.Trailers
import kotlinx.coroutines.flow.Flow

interface TrailerCache {

    fun insert(trailer: Trailers)

    fun insert(trailerList: List<Trailers>)

    fun getTrailersByShowId(showId: Long): Flow<List<SelectByShowId>>

}
