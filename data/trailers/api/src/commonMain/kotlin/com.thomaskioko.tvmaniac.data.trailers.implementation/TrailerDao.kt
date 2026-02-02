package com.thomaskioko.tvmaniac.data.trailers.implementation

import com.thomaskioko.tvmaniac.db.SelectByShowTraktId
import com.thomaskioko.tvmaniac.db.Trailers
import kotlinx.coroutines.flow.Flow

public interface TrailerDao {

    public fun upsert(trailer: Trailers)

    public fun getTrailersByShowTraktId(showTraktId: Long): List<SelectByShowTraktId>

    public fun observeTrailersByShowTraktId(showTraktId: Long): Flow<List<SelectByShowTraktId>>

    public fun delete(id: Long)

    public fun deleteAll()
}
