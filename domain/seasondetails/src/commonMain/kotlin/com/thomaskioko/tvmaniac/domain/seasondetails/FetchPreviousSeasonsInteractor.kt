package com.thomaskioko.tvmaniac.domain.seasondetails

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import me.tatarka.inject.annotations.Inject

@Inject
public class FetchPreviousSeasonsInteractor(
    private val seasonDetailsRepository: SeasonDetailsRepository,
) : Interactor<FetchPreviousSeasonsParams>() {
    override suspend fun doWork(params: FetchPreviousSeasonsParams) {
        seasonDetailsRepository.syncPreviousSeasonsEpisodes(
            showTraktId = params.showTraktId,
            beforeSeasonNumber = params.seasonNumber,
        )
    }
}

public data class FetchPreviousSeasonsParams(
    val showTraktId: Long,
    val seasonNumber: Long,
)
