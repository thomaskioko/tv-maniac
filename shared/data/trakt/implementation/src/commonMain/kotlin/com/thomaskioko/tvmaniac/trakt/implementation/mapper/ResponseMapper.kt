package com.thomaskioko.tvmaniac.trakt.implementation.mapper

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.db.Show_category
import com.thomaskioko.tvmaniac.core.util.FormatterUtil
import com.thomaskioko.tvmaniac.core.util.network.ApiResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.TraktShowsResponse

fun ApiResponse<List<TraktShowResponse>, ErrorResponse>.showResponseToCacheList() = when (this) {
    is ApiResponse.Success -> body.map { it.responseToCache() }
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

fun TraktShowResponse.responseToCache() = Show(
    trakt_id = ids.trakt.toLong(),
    tmdb_id = ids.tmdb?.toLong(),
    title = title,
    overview = overview ?: "",
    votes = votes.toLong(),
    year = year ?: "--",
    runtime = runtime.toLong(),
    aired_episodes = airedEpisodes.toLong(),
    language = language?.uppercase(),
    rating = rating.toTwoDecimalPoint(),
    genres = genres.map { it.replaceFirstChar { it.uppercase() } },
    status = status.replaceFirstChar { it.uppercase() },
)

fun ApiResponse<List<TraktShowsResponse>, ErrorResponse>.showsResponseToCacheList() = when (this) {
    is ApiResponse.Success -> body.map { it.showResponseToCacheList() }
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


fun TraktShowsResponse.showResponseToCacheList(): Show = Show(
    trakt_id = show.ids.trakt.toLong(),
    tmdb_id = show.ids.tmdb?.toLong(),
    title = show.title,
    overview = show.overview ?: "",
    votes = show.votes.toLong(),
    year = show.year ?: "--",
    runtime = show.runtime.toLong(),
    aired_episodes = show.airedEpisodes.toLong(),
    language = show.language?.uppercase(),
    rating = show.rating.toTwoDecimalPoint(),
    genres = show.genres.map { it.replaceFirstChar { it.uppercase() } },
    status = show.status.replaceFirstChar { it.uppercase() },
)


fun List<Show>.toCategoryCache(categoryId: Long) = map {
    Show_category(
        trakt_id = it.trakt_id,
        category_id = categoryId
    )
}

fun Double?.toTwoDecimalPoint() = FormatterUtil.formatDouble(this, 1)