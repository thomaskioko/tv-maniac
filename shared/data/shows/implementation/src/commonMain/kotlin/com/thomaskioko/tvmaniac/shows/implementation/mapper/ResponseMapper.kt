package com.thomaskioko.tvmaniac.shows.implementation.mapper

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.base.util.FormatterUtil
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.db.Show_category
import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowsResponse

//TODO:: Format to a class and inject it.

fun ApiResponse<List<TraktShowResponse>, ErrorResponse>.showResponseToCacheList(
    formatterUtil: FormatterUtil
) = when (this) {
    is ApiResponse.Success -> body.map { it.responseToCache(formatterUtil) }
    is ApiResponse.Error.GenericError -> {
        Logger.withTag("showResponseToCacheList").e("$this")
        throw Throwable("$errorMessage")
    }

    is ApiResponse.Error.HttpError -> {
        Logger.withTag("showResponseToCacheList").e("$this")
        throw Throwable("$code - ${errorBody?.message}")
    }

    is ApiResponse.Error.SerializationError -> {
        Logger.withTag("showResponseToCacheList").e("$this")
        throw Throwable("$this")
    }
}

fun TraktShowResponse.responseToCache(
    formatterUtil: FormatterUtil
) = Show(
    trakt_id = ids.trakt.toLong(),
    tmdb_id = ids.tmdb?.toLong(),
    title = title,
    overview = overview ?: "",
    votes = votes.toLong(),
    year = year ?: "--",
    runtime = runtime.toLong(),
    aired_episodes = airedEpisodes.toLong(),
    language = language?.uppercase(),
    rating = formatterUtil.formatDouble(rating, 1),
    genres = genres.map { it.replaceFirstChar { it.uppercase() } },
    status = status.replaceFirstChar { it.uppercase() },
)

fun ApiResponse<List<TraktShowsResponse>, ErrorResponse>.showsResponseToCacheList(
    formatterUtil: FormatterUtil
) = when (this) {
    is ApiResponse.Success -> body.map { it.showResponseToCacheList(formatterUtil) }
    is ApiResponse.Error.GenericError -> {
        Logger.withTag("showsResponseToCacheList").e("$this")
        throw Throwable("$errorMessage")
    }

    is ApiResponse.Error.HttpError -> {
        Logger.withTag("showsResponseToCacheList").e("$this")
        throw Throwable("$code - ${errorBody?.message}")
    }

    is ApiResponse.Error.SerializationError -> {
        Logger.withTag("showsResponseToCacheList").e("$this")
        throw Throwable("$this")
    }
}


fun TraktShowsResponse.showResponseToCacheList(
    formatterUtil: FormatterUtil
): Show = Show(
    trakt_id = show.ids.trakt.toLong(),
    tmdb_id = show.ids.tmdb?.toLong(),
    title = show.title,
    overview = show.overview ?: "",
    votes = show.votes.toLong(),
    year = show.year ?: "--",
    runtime = show.runtime.toLong(),
    aired_episodes = show.airedEpisodes.toLong(),
    language = show.language?.uppercase(),
    rating = formatterUtil.formatDouble(show.rating, 1),
    genres = show.genres.map { it.replaceFirstChar { it.uppercase() } },
    status = show.status.replaceFirstChar { it.uppercase() },
)


fun List<Show>.toCategoryCache(categoryId: Long) = map {
    Show_category(
        trakt_id = it.trakt_id,
        category_id = categoryId
    )
}