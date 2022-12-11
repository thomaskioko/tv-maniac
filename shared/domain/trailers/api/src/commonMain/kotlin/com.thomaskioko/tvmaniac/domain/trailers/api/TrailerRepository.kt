package com.thomaskioko.tvmaniac.domain.trailers.api

import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.util.network.Resource
import kotlinx.coroutines.flow.Flow

interface TrailerRepository {
    fun isWebViewInstalled() : Flow<Boolean>
    fun observeTrailersByShowId(traktId: Int): Flow<Resource<List<Trailers>>>
}