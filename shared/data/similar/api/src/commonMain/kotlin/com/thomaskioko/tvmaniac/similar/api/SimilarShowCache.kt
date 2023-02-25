package com.thomaskioko.tvmaniac.similar.api

import com.thomaskioko.tvmaniac.core.db.SelectSimilarShows
import kotlinx.coroutines.flow.Flow

interface SimilarShowCache {

    fun insert(traktId: Long, similarShowId: Long)

    fun observeSimilarShows(traktId: Long): Flow<List<SelectSimilarShows>>
}
