package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.db.SelectFollowedShows
import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
import kotlinx.coroutines.flow.Flow

interface ShowsRepository {

    fun observeFollowedShows(): Flow<Either<Failure, List<SelectFollowedShows>>>

    fun getFollowedShows(): List<SelectFollowedShows>

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

    suspend fun updateFollowedShow(traktId: Long, addToWatchList: Boolean)

    suspend fun fetchShows()

}