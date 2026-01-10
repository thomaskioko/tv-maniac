package com.thomaskioko.tvmaniac.data.trailers.implementation

import com.thomaskioko.tvmaniac.db.SelectByShowTmdbId
import com.thomaskioko.tvmaniac.db.Trailers
import kotlinx.coroutines.flow.Flow

public interface TrailerDao {

    public fun upsert(trailer: Trailers)

    public fun upsert(trailerList: List<Trailers>)

    public fun observeTrailersByShowTmdbId(showTmdbId: Long): Flow<List<SelectByShowTmdbId>>

    public fun delete(id: Long)

    public fun deleteAll()
}
