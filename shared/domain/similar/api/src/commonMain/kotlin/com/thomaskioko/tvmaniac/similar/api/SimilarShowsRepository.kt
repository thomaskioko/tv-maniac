package com.thomaskioko.tvmaniac.similar.api

import com.thomaskioko.tvmaniac.datasource.cache.SelectSimilarShows
import com.thomaskioko.tvmaniac.shared.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface SimilarShowsRepository {

    fun observeSimilarShows(showId: Long): Flow<Resource<List<SelectSimilarShows>>>
}
