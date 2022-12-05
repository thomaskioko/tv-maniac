package com.thomaskioko.tvmaniac.tmdb.api

import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.util.network.Resource
import kotlinx.coroutines.flow.Flow

interface TmdbRepository {

    fun observeShow(tmdbId: Int): Flow<Resource<SelectByShowId>>

    fun updateShowArtWork() : Flow<Resource<Unit>>

}
