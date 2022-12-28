package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.db.SelectFollowedShows
import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.db.TraktStats
import com.thomaskioko.tvmaniac.core.db.Trakt_list
import com.thomaskioko.tvmaniac.core.db.Trakt_user
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
import kotlinx.coroutines.flow.Flow

interface TraktRepository {
    fun observeMe(slug: String): Flow<Either<Failure, Trakt_user>>

    fun observeStats(slug: String, refresh: Boolean = false): Flow<Either<Failure, TraktStats>>

    fun observeCreateTraktList(userSlug: String): Flow<Either<Failure, Trakt_list>>

    fun observeFollowedShows(): Flow<Either<Failure, List<SelectFollowedShows>>>

    fun getFollowedShows(): List<SelectFollowedShows>

    fun observeUpdateFollowedShow(traktId: Int, addToWatchList: Boolean): Flow<Either<Failure, Unit>>

    fun observeShow(traktId: Int): Flow<Either<Failure, SelectByShowId>>

    fun fetchShowsByCategoryId(categoryId: Int): Flow<Either<Failure, List<SelectShowsByCategory>>>

    fun observeCachedShows(categoryId: Int): Flow<Either<Failure, List<SelectShowsByCategory>>>

    suspend fun updateFollowedShow(traktId: Int, addToWatchList: Boolean)

    suspend fun fetchTraktWatchlistShows()

    suspend fun fetchShows()

    suspend fun syncFollowedShows()

}