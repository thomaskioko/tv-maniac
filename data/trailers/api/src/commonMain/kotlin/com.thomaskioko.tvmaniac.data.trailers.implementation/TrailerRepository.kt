package com.thomaskioko.tvmaniac.data.trailers.implementation

import com.thomaskioko.tvmaniac.core.db.Trailers
import kotlinx.coroutines.flow.Flow
import org.mobilenativefoundation.store.store5.StoreReadResponse

interface TrailerRepository {
    fun isYoutubePlayerInstalled(): Flow<Boolean>
    fun observeTrailersStoreResponse(traktId: Long): Flow<StoreReadResponse<List<Trailers>>>
    suspend fun fetchTrailersByShowId(traktId: Long): List<Trailers>
}
