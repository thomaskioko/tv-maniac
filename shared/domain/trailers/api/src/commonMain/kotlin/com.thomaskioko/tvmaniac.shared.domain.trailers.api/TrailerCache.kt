package com.thomaskioko.tvmaniac.shared.domain.trailers.api

import com.thomaskioko.tvmaniac.core.db.Trailers
import kotlinx.coroutines.flow.Flow

interface TrailerCache {

    fun insert(trailer: Trailers)

    fun insert(trailerList: List<Trailers>)

    fun getTrailersByShowId(showId: Long): Flow<List<Trailers>>

}
