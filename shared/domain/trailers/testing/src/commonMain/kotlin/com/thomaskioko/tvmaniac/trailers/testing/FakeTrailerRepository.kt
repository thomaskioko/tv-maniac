package com.thomaskioko.tvmaniac.trailers.testing

import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.TrailerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeTrailerRepository : TrailerRepository {
    private var trailersResult: Flow<Resource<List<Trailers>>> =
        flowOf(Resource.Success(data = null))

    suspend fun setTrailerResult(result: Resource<List<Trailers>>) {
        trailersResult = flow { emit(result) }
    }

    override fun isWebViewInstalled(): Flow<Boolean> = flowOf(false)

    override fun observeTrailersByShowId(traktId: Int): Flow<Resource<List<Trailers>>> =
        trailersResult
}