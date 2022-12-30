package com.thomaskioko.tvmaniac.seasondetails.api

import com.thomaskioko.tvmaniac.core.db.Season
import com.thomaskioko.tvmaniac.core.db.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
import kotlinx.coroutines.flow.Flow

interface SeasonDetailsRepository {

    fun observeSeasons(traktId: Int): Flow<Either<Failure, List<Season>>>

    fun observeSeasonDetails(showId: Int): Flow<Either<Failure, List<SelectSeasonWithEpisodes>>>

    fun observeCachedSeasonDetails(showId: Int): Flow<Either<Failure, List<SelectSeasonWithEpisodes>>>

}
