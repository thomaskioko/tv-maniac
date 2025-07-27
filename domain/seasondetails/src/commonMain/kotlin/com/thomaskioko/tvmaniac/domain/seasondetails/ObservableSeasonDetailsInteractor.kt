package com.thomaskioko.tvmaniac.domain.seasondetails

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.data.cast.api.CastRepository
import com.thomaskioko.tvmaniac.domain.seasondetails.model.SeasonCast
import com.thomaskioko.tvmaniac.domain.seasondetails.model.SeasonDetailsResult
import com.thomaskioko.tvmaniac.domain.seasondetails.model.SeasonImages
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

@Inject
class ObservableSeasonDetailsInteractor(
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val castRepository: CastRepository,
) : SubjectInteractor<SeasonDetailsParam, SeasonDetailsResult>() {
    override fun createObservable(params: SeasonDetailsParam): Flow<SeasonDetailsResult> {
        return combine(
            seasonDetailsRepository.observeSeasonImages(params.seasonId),
            seasonDetailsRepository.observeSeasonDetails(params),
            castRepository.observeSeasonCast(params.seasonId),
        ) { images, seasonDetails, cast ->
            SeasonDetailsResult(
                seasonDetails = seasonDetails,
                images = images.map { SeasonImages(it.id, it.image_url) },
                cast = cast.map {
                    SeasonCast(
                        id = it.id.id,
                        name = it.name,
                        profilePath = it.profile_path,
                        characterName = it.character_name,
                    )
                },
            )
        }
    }
}
