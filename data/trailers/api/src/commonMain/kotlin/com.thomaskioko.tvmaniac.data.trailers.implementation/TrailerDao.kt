package com.thomaskioko.tvmaniac.data.trailers.implementation

import com.thomaskioko.tvmaniac.db.SelectByShowId
import com.thomaskioko.tvmaniac.db.Trailers
import kotlinx.coroutines.flow.Flow

public interface TrailerDao {

    public fun upsert(trailer: Trailers)

    public fun getTrailersByShowId(showId: Long): List<SelectByShowId>

    public fun observeTrailersByShowId(showId: Long): Flow<List<SelectByShowId>>

    public fun delete(id: Long)

    public fun deleteAll()
}
