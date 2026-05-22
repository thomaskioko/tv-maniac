package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.cast.api.CastRepository
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import com.thomaskioko.tvmaniac.domain.showdetails.model.ShowMetadata
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

@Inject
public class ObservableShowMetadataInteractor(
    private val castRepository: CastRepository,
    private val episodeRepository: EpisodeRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val seasonsRepository: SeasonsRepository,
    private val similarShowsRepository: SimilarShowsRepository,
    private val trailerRepository: TrailerRepository,
    private val watchProviderRepository: WatchProviderRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : SubjectInteractor<Long, ShowMetadata>() {

    override fun createObservable(params: Long): Flow<ShowMetadata> {
        return combine(
            seasonsRepository.observeSeasonsByShowId(params),
            castRepository.observeShowCast(params),
            watchProviderRepository.observeWatchProviders(params),
            similarShowsRepository.observeSimilarShows(params),
            trailerRepository.observeTrailers(params),
            trailerRepository.isYoutubePlayerInstalled(),
            episodeRepository.observeAllSeasonsWatchProgress(params),
            seasonDetailsRepository.observeContinueTrackingEpisodes(params),
        ) { seasonsList, castList, watchProviders, similarShows,
            trailers, isWebViewInstalled, seasonsProgress, continueTracking,
            ->
            val progressMap = seasonsProgress.associateBy { it.seasonNumber }
            ShowMetadata(
                providers = watchProviders.toWatchProviderList(),
                castsList = castList.toCastList(),
                seasonsList = seasonsList.toSeasonsList(progressMap),
                similarShows = similarShows.toSimilarShowList(),
                trailersList = trailers.toTrailerList(),
                hasWebViewInstalled = isWebViewInstalled,
                continueTrackingEpisodes = continueTracking?.episodes ?: persistentListOf(),
            )
        }.flowOn(dispatchers.io.limitedParallelism(8))
    }
}
