package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.cast.api.CastRepository
import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsRepository
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import com.thomaskioko.tvmaniac.domain.showdetails.model.ShowDetails
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.util.FormatterUtil
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

@Inject
class ObservableShowDetailsInteractor(
    private val castRepository: CastRepository,
    private val recommendedShowsRepository: RecommendedShowsRepository,
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
            recommendedShowsRepository.observeRecommendedShows(params),
            seasonsRepository.observeSeasonsByShowId(params),
            castRepository.observeShowCast(params),
            watchProviders.observeWatchProviders(params),
            similarShowsRepository.observeSimilarShows(params),
            trailerRepository.observeTrailers(params),
            trailerRepository.isYoutubePlayerInstalled(),
        ) { showDetails, recommendedShows, seasonsList, castList, watchProviders, similarShows, trailers, isWebViewInstalled ->
            ShowDetails(
                tmdbId = showDetails.id.id,
                title = showDetails.name,
                overview = showDetails.overview,
                language = showDetails.language,
                posterImageUrl = showDetails.poster_path,
                backdropImageUrl = showDetails.backdrop_path,
                votes = showDetails.vote_count,
                rating = formatterUtil.formatDouble(showDetails.vote_average, 1),
                year = showDetails.last_air_date ?: showDetails.first_air_date ?: "",
                status = showDetails.status,
                isInLibrary = showDetails.in_library == 1L,
                hasWebViewInstalled = isWebViewInstalled,
                genres = showDetails.genre_list.toGenreList(),
                providers = watchProviders.toWatchProviderList(),
                castsList = castList.toCastList(),
                seasonsList = seasonsList.toSeasonsList(),
                similarShows = similarShows.toSimilarShowList(),
                recommendedShows = recommendedShows.toRecommendedShowList(),
                trailersList = trailers.toTrailerList(),
            )
        }.flowOn(dispatchers.io.limitedParallelism(7))
    }
}
