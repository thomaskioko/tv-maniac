package com.thomaskioko.tvmaniac.data.trailers.implementation

import com.thomaskioko.tvmaniac.db.Trailers
import kotlinx.coroutines.flow.Flow

public interface TrailerDao {

    public fun upsert(trailer: Trailers)

    public fun upsert(trailerList: List<Trailers>)

    public fun observeTrailersById(showId: Long): Flow<List<Trailers>>

    public fun getTrailersById(showId: Long): List<Trailers>

    public fun delete(id: Long)

    public fun deleteAll()
}
