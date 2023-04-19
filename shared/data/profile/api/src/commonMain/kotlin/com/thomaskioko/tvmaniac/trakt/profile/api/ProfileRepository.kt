package com.thomaskioko.tvmaniac.trakt.profile.api

import com.thomaskioko.tvmaniac.core.db.Trakt_shows_list
import com.thomaskioko.tvmaniac.core.db.Trakt_user
import com.thomaskioko.tvmaniac.core.db.User_stats
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    fun observeMe(slug: String): Flow<Either<Failure, Trakt_user>>

    fun observeStats(slug: String, refresh: Boolean = false): Flow<Either<Failure, User_stats>>

    fun observeCreateTraktList(userSlug: String): Flow<Either<Failure, Trakt_shows_list>>

    fun observeUpdateFollowedShow(traktId: Long, addToWatchList: Boolean): Flow<Either<Failure, Unit>>

    suspend fun syncFollowedShows()

    suspend fun fetchTraktWatchlistShows()
}
