package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.cast.api.CastRepository
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import com.thomaskioko.tvmaniac.domain.showdetails.model.ShowDetails
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject

@Inject
public class ObservableShowDetailsInteractor(
    private val castRepository: CastRepository,
    private val episodeRepository: EpisodeRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val seasonsRepository: SeasonsRepository,
    private val showDetailsRepository: ShowDetailsRepository,
    private val similarShowsRepository: SimilarShowsRepository,
    private val trailerRepository: TrailerRepository,
    private val watchProviders: WatchProviderRepository,
    private val formatterUtil: FormatterUtil,
    private val dispatchers: AppCoroutineDispatchers,
) : SubjectInteractor<Long, ShowDetails>() {
    override fun createObservable(params: Long): Flow<ShowDetails> {
        return combine(
            showDetailsRepository.observeShowDetails(params),
            seasonsRepository.observeSeasonsByShowId(params),
            castRepository.observeShowCast(params),
            watchProviders.observeWatchProviders(params),
            similarShowsRepository.observeSimilarShows(params),
            trailerRepository.observeTrailers(params),
            trailerRepository.isYoutubePlayerInstalled(),
            episodeRepository.observeAllSeasonsWatchProgress(params),
            seasonDetailsRepository.observeContinueTrackingEpisodes(params),
        ) { showDetails, seasonsList, castList, watchProviders, similarShows,
            trailers, isWebViewInstalled, seasonsProgress, continueTracking,
            ->
            val progressMap = seasonsProgress.associateBy { it.seasonNumber }
            ShowDetails(
                traktId = showDetails.trakt_id.id,
                tmdbId = showDetails.tmdb_id.id,
                title = showDetails.name,
                overview = showDetails.overview,
                language = showDetails.language,
                posterImageUrl = showDetails.poster_path,
                backdropImageUrl = showDetails.backdrop_path,
                votes = showDetails.vote_count,
                rating = formatterUtil.formatDouble(showDetails.ratings, 1),
                year = showDetails.year ?: "",
                status = showDetails.status,
                isInLibrary = showDetails.in_library == 1L,
                hasWebViewInstalled = isWebViewInstalled,
                genres = showDetails.genres ?: emptyList(),
                providers = watchProviders.toWatchProviderList(),
                castsList = castList.toCastList(),
                seasonsList = seasonsList.toSeasonsList(progressMap),
                similarShows = similarShows.toSimilarShowList(),
                trailersList = trailers.toTrailerList(),
                continueTrackingEpisodes = continueTracking?.episodes ?: persistentListOf(),
            )
        }.flowOn(dispatchers.io.limitedParallelism(8))
    }
}
