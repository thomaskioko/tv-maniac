package com.thomaskioko.tvmaniac.topratedshows.data.api

import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow

interface TopRatedShowsRepository {
    suspend fun fetchTopRatedShows(): List<ShowEntity>
    fun observeTopRatedShows(): Flow<Either<Failure, List<ShowEntity>>>
}
