package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.category.api.model.Category
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.db.ShowById
import com.thomaskioko.tvmaniac.core.db.Show_category
import com.thomaskioko.tvmaniac.core.db.ShowsByCategory
import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.resourcemanager.api.LastRequest
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.trakt.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowsResponse
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.KermitLogger
import me.tatarka.inject.annotations.Inject

@Inject
class DiscoverResponseMapper(
    private val requestManagerRepository: RequestManagerRepository,
    private val formatterUtil: FormatterUtil,
    private val logger: KermitLogger,
) {

    fun showResponseToCacheList(
        category: Category,
        response: ApiResponse<List<TraktShowResponse>, ErrorResponse>,
    ) = when (response) {
        is ApiResponse.Success -> {
            category.insertRequest(requestManagerRepository)
            response.body.map { responseToEntity(it) }
        }

        is ApiResponse.Error.HttpError -> {
            logger.error("ShowStore GenericError", "${response.errorBody}")
            throw Throwable("${response.errorBody}")
        }
        is ApiResponse.Error.GenericError -> {
            logger.error("ShowStore GenericError", "${response.errorMessage}")
            throw Throwable("${response.errorMessage}")
        }
        is ApiResponse.Error.SerializationError -> {
            logger.error("ShowStore GenericError", "${response.errorMessage}")
            throw Throwable("${response.errorMessage}")
        }
    }

    fun responseToEntityList(
        category: Category,
        response: ApiResponse<List<TraktShowsResponse>, ErrorResponse>,
    ) = when (response) {
        is ApiResponse.Success -> {
            category.insertRequest(requestManagerRepository)
            response.body.map { showResponseToCacheList(it) }
        }

        is ApiResponse.Error.HttpError -> {
            logger.error("ShowStore GenericError", "${response.errorBody}")
            throw Throwable("${response.errorBody}")
        }
        is ApiResponse.Error.SerializationError -> {
            logger.error("ShowStore GenericError", "${response.errorMessage}")
            throw Throwable("${response.errorMessage}")
        }
        is ApiResponse.Error.GenericError -> {
            logger.error("ShowStore GenericError", "${response.errorMessage}")
            throw Throwable("${response.errorMessage}")
        }
    }

    private fun responseToEntity(response: TraktShowResponse) = ShowsByCategory(
        trakt_id = response.ids.trakt.toLong(),
        tmdb_id = response.ids.tmdb?.toLong(),
        title = response.title,
        overview = response.overview ?: "",
        votes = response.votes.toLong(),
        year = response.year?.toString() ?: "--",
        runtime = response.runtime.toLong(),
        aired_episodes = response.airedEpisodes.toLong(),
        language = response.language?.uppercase(),
        rating = formatterUtil.formatDouble(response.rating, 1),
        genres = response.genres.map { it.replaceFirstChar { it.uppercase() } },
        status = response.status.replaceFirstChar { it.uppercase() },
        poster_url = null,
        backdrop_url = null,
        category_id = null,
    )

    fun responseToShow(response: TraktShowResponse) = ShowById(
        trakt_id = response.ids.trakt.toLong(),
        tmdb_id = response.ids.tmdb?.toLong(),
        title = response.title,
        overview = response.overview ?: "",
        votes = response.votes.toLong(),
        year = response.year?.toString() ?: "--",
        runtime = response.runtime.toLong(),
        aired_episodes = response.airedEpisodes.toLong(),
        language = response.language?.uppercase(),
        rating = formatterUtil.formatDouble(response.rating, 1),
        genres = response.genres.map { it.replaceFirstChar { it.uppercase() } },
        status = response.status.replaceFirstChar { it.uppercase() },
        trakt_id_ = null,
        tmdb_id_ = null,
        poster_url = null,
        backdrop_url = null,
        id = null,
        created_at = null,
        synced = false,
    )

    fun toShow(showById: ShowById) =
        Show(
            trakt_id = showById.trakt_id,
            tmdb_id = showById.tmdb_id,
            title = showById.title,
            overview = showById.overview,
            language = showById.language,
            year = showById.year,
            rating = showById.rating,
            status = showById.status,
            runtime = showById.runtime,
            votes = showById.votes,
            aired_episodes = showById.aired_episodes,
            genres = showById.genres,
        )

    private fun showResponseToCacheList(response: TraktShowsResponse): ShowsByCategory =
        ShowsByCategory(
            trakt_id = response.show.ids.trakt.toLong(),
            tmdb_id = response.show.ids.tmdb?.toLong(),
            title = response.show.title,
            overview = response.show.overview ?: "",
            votes = response.show.votes.toLong(),
            year = response.show.year?.toString() ?: "--",
            runtime = response.show.runtime.toLong(),
            aired_episodes = response.show.airedEpisodes.toLong(),
            language = response.show.language?.uppercase(),
            rating = formatterUtil.formatDouble(response.show.rating, 1),
            genres = response.show.genres.map { it.replaceFirstChar { it.uppercase() } },
            status = response.show.status.replaceFirstChar { it.uppercase() },
            poster_url = null,
            backdrop_url = null,
            category_id = null,
        )

    fun toCategoryCache(shows: List<Show>, categoryId: Long) = shows.map {
        Show_category(
            trakt_id = it.trakt_id,
            category_id = categoryId,
        )
    }
}

private fun Category.insertRequest(requestManagerRepository: RequestManagerRepository) {
    requestManagerRepository.insert(
        LastRequest(
            id = id,
            entityId = id,
            requestType = title,
        ),
    )
}
