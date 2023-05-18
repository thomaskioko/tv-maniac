package com.thomaskioko.tvmaniac.similar.api

import com.thomaskioko.tvmaniac.core.db.SimilarShows
import kotlinx.coroutines.flow.Flow
import org.mobilenativefoundation.store.store5.StoreReadResponse

interface SimilarShowsRepository {

    fun observeSimilarShows(traktId: Long): Flow<StoreReadResponse<List<SimilarShows>>>
}
