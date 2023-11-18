package com.thomaskioko.tvmaniac.seasondetails.api

import com.thomaskioko.tvmaniac.core.db.SeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import kotlinx.coroutines.flow.Flow

interface SeasonDetailsRepository {

    fun observeSeasonDetailsStream(traktId: Long): Flow<Either<Failure, List<SeasonWithEpisodes>>>

    fun observeCachedSeasonDetails(traktId: Long): Flow<Either<Failure, List<SeasonWithEpisodes>>>
}
