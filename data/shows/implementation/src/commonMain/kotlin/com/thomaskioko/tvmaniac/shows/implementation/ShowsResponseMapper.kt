package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.db.Show_category
import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowsResponse
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.KermitLogger
import me.tatarka.inject.annotations.Inject

@Inject
class ShowsResponseMapper(
    private val formatterUtil: FormatterUtil,
    private val logger: KermitLogger,
) {

    fun showResponseToCacheList(result: ApiResponse<List<TraktShowResponse>, ErrorResponse>) =
        when (result) {
            is ApiResponse.Success -> result.body.map { responseToCache(it) }
            is ApiResponse.Error.GenericError -> {
                logger.error("showResponseToCacheList", "$this")
                throw Throwable("${result.errorMessage}")
            }

            is ApiResponse.Error.HttpError -> {
                logger.error("showResponseToCacheList", "$this")
                throw Throwable("${result.code} - ${result.errorBody?.message}")
            }

            is ApiResponse.Error.SerializationError -> {
                logger.error("showResponseToCacheList", "$this")
                throw Throwable("$this")
            }
        }

    fun responseToCache(response: TraktShowResponse) = Show(
        trakt_id = response.ids.trakt.toLong(),
        tmdb_id = response.ids.tmdb?.toLong(),
        title = response.title,
        overview = response.overview ?: "",
        votes = response.votes.toLong(),
        year = response.year ?: "--",
        runtime = response.runtime.toLong(),
        aired_episodes = response.airedEpisodes.toLong(),
        language = response.language?.uppercase(),
        rating = formatterUtil.formatDouble(response.rating, 1),
        genres = response.genres.map { it.replaceFirstChar { it.uppercase() } },
        status = response.status.replaceFirstChar { it.uppercase() },
    )

    fun showsResponseToCacheList(result: ApiResponse<List<TraktShowsResponse>, ErrorResponse>) =
        when (result) {
            is ApiResponse.Success -> result.body.map { showResponseToCacheList(it) }
            is ApiResponse.Error.GenericError -> {
                logger.error("showsResponseToCacheList", "$this")
                throw Throwable("${result.errorMessage}")
            }

            is ApiResponse.Error.HttpError -> {
                logger.error("showsResponseToCacheList", "$this")
                throw Throwable("${result.code} - ${result.errorBody?.message}")
            }

            is ApiResponse.Error.SerializationError -> {
                logger.error("showsResponseToCacheList", "$this")
                throw Throwable("$this")
            }
        }

    private fun showResponseToCacheList(response: TraktShowsResponse): Show = Show(
        trakt_id = response.show.ids.trakt.toLong(),
        tmdb_id = response.show.ids.tmdb?.toLong(),
        title = response.show.title,
        overview = response.show.overview ?: "",
        votes = response.show.votes.toLong(),
        year = response.show.year ?: "--",
        runtime = response.show.runtime.toLong(),
        aired_episodes = response.show.airedEpisodes.toLong(),
        language = response.show.language?.uppercase(),
        rating = formatterUtil.formatDouble(response.show.rating, 1),
        genres = response.show.genres.map { it.replaceFirstChar { it.uppercase() } },
        status = response.show.status.replaceFirstChar { it.uppercase() },
    )

    fun toCategoryCache(shows: List<Show>, categoryId: Long) = shows.map {
        Show_category(
            trakt_id = it.trakt_id,
            category_id = categoryId,
        )
    }
}
