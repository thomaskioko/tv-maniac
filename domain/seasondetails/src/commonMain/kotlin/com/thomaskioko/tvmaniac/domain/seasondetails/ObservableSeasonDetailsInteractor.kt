package com.thomaskioko.tvmaniac.domain.seasondetails

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.data.cast.api.CastRepository
import com.thomaskioko.tvmaniac.domain.seasondetails.model.SeasonCast
import com.thomaskioko.tvmaniac.domain.seasondetails.model.SeasonDetailsResult
import com.thomaskioko.tvmaniac.domain.seasondetails.model.SeasonImages
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapNotNull
import me.tatarka.inject.annotations.Inject

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
            seasonDetails?.let { details ->
                SeasonDetailsResult(
                    seasonDetails = details,
                    images = images.map { image -> SeasonImages(image.id, image.image_url) },
                    cast = cast.map { castMember ->
                        SeasonCast(
                            id = castMember.id.id,
                            name = castMember.name,
                            profilePath = castMember.profile_path,
                            characterName = castMember.character_name,
                        )
                    },
                )
            }
        }.mapNotNull { it }
    }
}
