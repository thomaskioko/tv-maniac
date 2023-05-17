package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import kotlinx.coroutines.flow.Flow

interface ShowsRepository {

    fun observeShow(traktId: Long): Flow<Either<Failure, SelectByShowId>>

    fun observeCachedShows(categoryId: Long): Flow<Either<Failure, List<SelectShowsByCategory>>>

    fun fetchTrendingShows(): Flow<Either<Failure, List<SelectShowsByCategory>>>

    fun observeTrendingCachedShows(): Flow<Either<Failure, List<SelectShowsByCategory>>>

    fun fetchPopularShows(): Flow<Either<Failure, List<SelectShowsByCategory>>>

    fun observePopularCachedShows(): Flow<Either<Failure, List<SelectShowsByCategory>>>

    fun fetchAnticipatedShows(): Flow<Either<Failure, List<SelectShowsByCategory>>>

    fun observeAnticipatedCachedShows(): Flow<Either<Failure, List<SelectShowsByCategory>>>

    fun fetchFeaturedShows(): Flow<Either<Failure, List<SelectShowsByCategory>>>

    fun observeFeaturedCachedShows(): Flow<Either<Failure, List<SelectShowsByCategory>>>

    suspend fun fetchShows()
}
