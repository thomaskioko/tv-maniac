package com.thomaskioko.tvmaniac.data.popularshows.api

import com.thomaskioko.tvmaniac.core.db.PagedPopularShows
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow

interface PopularShowsRepository {
    suspend fun fetchPopularShows(): List<PagedPopularShows>
    fun observePopularShows(): Flow<Either<Failure, List<PagedPopularShows>>>
}
