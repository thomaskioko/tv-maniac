package com.thomaskioko.tvmaniac.profile.api.followed

import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import kotlinx.coroutines.flow.Flow

//TODO:: Move to separate module. #82
interface FollowedRepository {

    fun observeUpdateFollowedShow(traktId: Long, addToWatchList: Boolean): Flow<Either<Failure, Unit>>

    suspend fun syncFollowedShows()

}
