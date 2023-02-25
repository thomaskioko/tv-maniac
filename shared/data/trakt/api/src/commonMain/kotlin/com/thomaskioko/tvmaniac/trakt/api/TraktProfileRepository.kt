package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.db.TraktStats
import com.thomaskioko.tvmaniac.core.db.Trakt_list
import com.thomaskioko.tvmaniac.core.db.Trakt_user
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
import kotlinx.coroutines.flow.Flow

interface TraktProfileRepository {

    fun observeMe(slug: String): Flow<Either<Failure, Trakt_user>>

    fun observeStats(slug: String, refresh: Boolean = false): Flow<Either<Failure, TraktStats>>

    fun observeCreateTraktList(userSlug: String): Flow<Either<Failure, Trakt_list>>

    fun observeUpdateFollowedShow(traktId: Long, addToWatchList: Boolean): Flow<Either<Failure, Unit>>

}