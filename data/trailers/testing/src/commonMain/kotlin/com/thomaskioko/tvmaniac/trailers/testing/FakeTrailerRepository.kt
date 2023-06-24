package com.thomaskioko.tvmaniac.trailers.testing

import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.mobilenativefoundation.store.store5.StoreReadResponse

class FakeTrailerRepository : TrailerRepository {
    private var trailerList = mutableListOf<Trailers>()
    private var trailersStoreResponse: Flow<StoreReadResponse<List<Trailers>>> = flowOf()

    suspend fun setTrailerResult(result: StoreReadResponse<List<Trailers>>) {
        trailersStoreResponse = flow { emit(result) }
    }

    fun setTrailerList(list: List<Trailers>) {
        trailerList.clear()
        trailerList.addAll(list.toMutableList())
    }

    override fun isYoutubePlayerInstalled(): Flow<Boolean> = flowOf()

    override fun observeTrailersStoreResponse(traktId: Long): Flow<StoreReadResponse<List<Trailers>>> =
        trailersStoreResponse

    override suspend fun fetchTrailersByShowId(traktId: Long): List<Trailers> = trailerList
}
