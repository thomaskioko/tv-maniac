package com.thomaskioko.tvmaniac.similar.api

import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.datasource.cache.SelectSimilarShows
import kotlinx.coroutines.flow.Flow

interface SimilarShowsRepository {

    fun observeSimilarShows(showId: Long): Flow<Resource<List<SelectSimilarShows>>>
}
