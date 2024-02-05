package com.thomaskioko.tvmaniac.data.trailers.implementation

import com.thomaskioko.tvmaniac.core.db.Trailers
import kotlinx.coroutines.flow.Flow

interface TrailerDao {

  fun upsert(trailer: Trailers)

  fun upsert(trailerList: List<Trailers>)

  fun observeTrailersById(showId: Long): Flow<List<Trailers>>

  fun getTrailersById(showId: Long): List<Trailers>

  fun delete(id: Long)

  fun deleteAll()
}
