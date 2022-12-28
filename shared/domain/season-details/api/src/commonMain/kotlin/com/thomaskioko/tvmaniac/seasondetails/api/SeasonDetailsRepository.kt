package com.thomaskioko.tvmaniac.seasondetails.api

import com.thomaskioko.tvmaniac.core.db.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.db.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
import kotlinx.coroutines.flow.Flow

interface SeasonDetailsRepository {

    fun observeShowSeasons(traktId: Int): Flow<Either<Failure, List<SelectSeasonsByShowId>>>

    fun observeSeasonEpisodes(showId: Int): Flow<Either<Failure, List<SelectSeasonWithEpisodes>>>

    fun getSeasonEpisodes(
        showId: Int,
    ): Flow<Either<Failure, List<SelectSeasonWithEpisodes>>>
}
