package com.thomaskioko.tvmaniac.shows.api.repository

import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.util.network.Resource
import kotlinx.coroutines.flow.Flow

interface TmdbRepository {

    fun observeShow(tmdbId: Int): Flow<Resource<out SelectByShowId?>>

    fun updateShowArtWork() : Flow<Resource<Unit>>

}
