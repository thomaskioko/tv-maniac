package com.thomaskioko.tvmaniac.domain.ratings

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.data.ratings.api.RatingEntityType
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
public class ObserveRatingTargetInteractor(
    private val showDetailsRepository: ShowDetailsRepository,
    private val seasonsRepository: SeasonsRepository,
    private val episodeRepository: EpisodeRepository,
) : SubjectInteractor<ObserveRatingTargetInteractor.Param, RatingTarget?>() {

    override fun createObservable(params: Param): Flow<RatingTarget?> = when (params.type) {
        RatingEntityType.SHOW -> showDetailsRepository.observeShowDetails(params.id).map { show ->
            RatingTarget.Show(
                title = show.name,
                year = show.year?.takeIf { it.isNotBlank() },
            )
        }
        RatingEntityType.SEASON -> seasonsRepository.observeSeasonById(params.id).map { season ->
            season?.let {
                RatingTarget.Season(
                    title = it.title,
                    showName = it.show_name,
                )
            }
        }
        RatingEntityType.EPISODE -> episodeRepository.observeEpisodeById(params.id).map { episode ->
            episode?.let {
                RatingTarget.Episode(
                    title = it.title,
                    showName = it.show_name,
                    seasonNumber = it.season_number,
                    episodeNumber = it.episode_number,
                )
            }
        }
    }

    public data class Param(
        val type: RatingEntityType,
        val id: Long,
    )
}
