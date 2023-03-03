package com.thomaskioko.tvmaniac.seasondetails.api

import com.thomaskioko.tvmaniac.core.db.Season
import com.thomaskioko.tvmaniac.core.db.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
import kotlinx.coroutines.flow.Flow

interface SeasonDetailsRepository {

    fun observeSeasonsStream(traktId: Long): Flow<Either<Failure, List<Season>>>

    fun observeSeasonDetailsStream(traktId: Long): Flow<Either<Failure, List<SelectSeasonWithEpisodes>>>

    fun observeSeasonDetails(): Flow<Either<Failure, List<SelectSeasonWithEpisodes>>>

}
