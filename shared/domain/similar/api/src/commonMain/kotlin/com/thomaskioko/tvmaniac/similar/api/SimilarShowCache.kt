package com.thomaskioko.tvmaniac.similar.api

import com.thomaskioko.tvmaniac.core.db.SelectSimilarShows
import kotlinx.coroutines.flow.Flow

interface SimilarShowCache {

    fun insert(traktId: Int, similarShowId: Int)

    fun observeSimilarShows(traktId: Int): Flow<List<SelectSimilarShows>>
}
