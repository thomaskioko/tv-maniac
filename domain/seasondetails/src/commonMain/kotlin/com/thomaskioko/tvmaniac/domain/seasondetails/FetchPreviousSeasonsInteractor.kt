package com.thomaskioko.tvmaniac.domain.seasondetails

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import dev.zacsweers.metro.Inject

@Inject
public class FetchPreviousSeasonsInteractor(
    private val seasonDetailsRepository: SeasonDetailsRepository,
) : Interactor<FetchPreviousSeasonsParams>() {
    override suspend fun doWork(params: FetchPreviousSeasonsParams) {
        seasonDetailsRepository.syncPreviousSeasonsEpisodes(
            showId = params.showId,
            beforeSeasonNumber = params.seasonNumber,
        )
    }
}

public data class FetchPreviousSeasonsParams(
    val showId: Long,
    val seasonNumber: Long,
)
