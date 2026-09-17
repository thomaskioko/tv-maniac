package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.CreditsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.GenreResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.NetworksResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowDetailsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResult
import com.thomaskioko.tvmaniac.tmdb.api.model.VideosResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.WatchProvidersResult
import com.thomaskioko.tvmaniac.trakt.api.TimePeriod
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktGenreResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSearchResult
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowPeopleResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktVideosResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedProgressResponse

internal object ImmediateTransactionRunner : DatabaseTransactionRunner {
    override fun <T> invoke(block: () -> T): T = block()
}

internal class FakeTraktShowsRemoteDataSource : TraktShowsRemoteDataSource {
    private var popularShowsResponse: ApiResponse<List<TraktShowResponse>>? = null

    fun setPopularShows(response: ApiResponse<List<TraktShowResponse>>) {
        popularShowsResponse = response
    }

    override suspend fun getRelatedShows(showId: Long, page: Int, limit: Int): ApiResponse<List<TraktShowResponse>> = error("not configured")
    override suspend fun getTrendingShows(page: Int, limit: Int, genres: String?): ApiResponse<List<TraktShowsResponse>> = error("not configured")
    override suspend fun getGenres(): ApiResponse<List<TraktGenreResponse>> = error("not configured")
    override suspend fun getPopularShows(page: Int, limit: Int, genres: String?): ApiResponse<List<TraktShowResponse>> =
        popularShowsResponse ?: error("FakeTraktShowsRemoteDataSource: getPopularShows not configured")
    override suspend fun getFavoritedShows(page: Int, limit: Int, period: TimePeriod, genres: String?): ApiResponse<List<TraktShowsResponse>> = error("not configured")
    override suspend fun getMostWatchedShows(page: Int, limit: Int, period: TimePeriod, genres: String?): ApiResponse<List<TraktShowsResponse>> = error("not configured")
    override suspend fun getShowDetails(showId: Long): ApiResponse<TraktShowResponse> = error("not configured")
    override suspend fun getShowByTmdbId(tmdbId: Long): ApiResponse<List<TraktSearchResult>> = error("not configured")
    override suspend fun searchShows(query: String, page: Int, limit: Int): ApiResponse<List<TraktSearchResult>> = error("not configured")
    override suspend fun getShowPeople(showId: Long): ApiResponse<TraktShowPeopleResponse> = error("not configured")
    override suspend fun getShowVideos(showId: Long): ApiResponse<List<TraktVideosResponse>> = error("not configured")
    override suspend fun getWatchedProgress(showId: Long): ApiResponse<TraktWatchedProgressResponse> = error("not configured")
}

internal class FakeTmdbDetailsSource : TmdbShowDetailsNetworkDataSource {
    private val detailsResponses = mutableMapOf<Long, ApiResponse<TmdbShowDetailsResponse>>()

    fun setShowDetails(id: Long, response: ApiResponse<TmdbShowDetailsResponse>) {
        detailsResponses[id] = response
    }

    override suspend fun getShowDetails(id: Long): ApiResponse<TmdbShowDetailsResponse> =
        detailsResponses[id] ?: ApiResponse.Error.HttpError(code = 404, errorBody = null, errorMessage = "not found")
    override suspend fun getSimilarShows(id: Long, page: Long): ApiResponse<TmdbShowResult> = error("not configured")
    override suspend fun getRecommendedShows(id: Long, page: Long): ApiResponse<TmdbShowResult> = error("not configured")
    override suspend fun getShowWatchProviders(id: Long): ApiResponse<WatchProvidersResult> = error("not configured")
}

internal fun buildTmdbDetails(id: Int, name: String): TmdbShowDetailsResponse = TmdbShowDetailsResponse(
    adult = false,
    backdropPath = null,
    episodeRunTime = arrayListOf(),
    firstAirDate = "2023-01-01",
    genres = arrayListOf(GenreResponse(id = 1, name = "Drama")),
    id = id,
    lastAirDate = null,
    lastEpisodeToAir = null,
    name = name,
    nextEpisodeToAir = null,
    networks = arrayListOf(NetworksResponse(id = 1, name = "Netflix")),
    numberOfEpisodes = 10,
    numberOfSeasons = 1,
    overview = "An overview",
    popularity = 80.0,
    posterPath = null,
    seasons = arrayListOf(),
    status = "Ended",
    voteAverage = 8.0,
    voteCount = 500,
    videos = VideosResponse(results = arrayListOf()),
    credits = CreditsResponse(cast = arrayListOf()),
    originalLanguage = "en",
)
