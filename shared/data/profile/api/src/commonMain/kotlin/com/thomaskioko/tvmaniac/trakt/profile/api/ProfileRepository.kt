package com.thomaskioko.tvmaniac.trakt.profile.api

import com.thomaskioko.tvmaniac.core.db.TraktStats
import com.thomaskioko.tvmaniac.core.db.Trakt_list
import com.thomaskioko.tvmaniac.core.db.Trakt_user
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    fun observeMe(slug: String): Flow<Either<Failure, Trakt_user>>

    fun observeStats(slug: String, refresh: Boolean = false): Flow<Either<Failure, TraktStats>>

    fun observeCreateTraktList(userSlug: String): Flow<Either<Failure, Trakt_list>>

    fun observeUpdateFollowedShow(traktId: Long, addToWatchList: Boolean): Flow<Either<Failure, Unit>>

    suspend fun syncFollowedShows()

    suspend fun fetchTraktWatchlistShows()
}