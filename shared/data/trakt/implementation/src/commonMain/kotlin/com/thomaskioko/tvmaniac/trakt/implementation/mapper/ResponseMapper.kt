package com.thomaskioko.tvmaniac.trakt.implementation.mapper

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.Followed_shows
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.db.Show_category
import com.thomaskioko.tvmaniac.core.db.TraktStats
import com.thomaskioko.tvmaniac.core.db.Trakt_list
import com.thomaskioko.tvmaniac.core.db.Trakt_user
import com.thomaskioko.tvmaniac.core.util.DateUtil
import com.thomaskioko.tvmaniac.core.util.FormatterUtil
import com.thomaskioko.tvmaniac.core.util.FormatterUtil.formatDuration
import com.thomaskioko.tvmaniac.core.util.network.ApiResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.TraktCreateListResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.TraktFollowedShowResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.TraktShowsResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.TraktUserResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.TraktUserStatsResponse
import kotlin.math.roundToInt

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


fun TraktUserStatsResponse.toCache(slug: String) = TraktStats(
    user_slug = slug,
    collected_shows = shows.collected.toString(),
    months = (episodes.minutes / 43800).toDouble().roundToInt().toString(),
    days = (episodes.minutes / 1440).toDouble().roundToInt().toString(),
    hours = formatDuration(episodes.minutes / 60),
    episodes_watched = formatDuration(episodes.watched)
)

fun TraktCreateListResponse.toCache() = Trakt_list(
    id = ids.trakt.toLong(),
    slug = ids.slug,
    description = description
)

fun TraktUserResponse.toCache(slug: String) = Trakt_user(
    slug = ids.slug,
    full_name = name,
    user_name = userName,
    profile_picture = images.avatar.full,
    is_me = slug == "me"
)

fun List<Show>.toCategoryCache(categoryId: Long) = map {
    Show_category(
        trakt_id = it.trakt_id,
        category_id = categoryId
    )
}

fun List<TraktFollowedShowResponse>.responseToCache() = map {
    Followed_shows(
        id = it.show.ids.trakt.toLong(),
        synced = true,
        created_at = DateUtil.getTimestampMilliseconds()
    )
}


fun Double?.toTwoDecimalPoint() = FormatterUtil.formatDouble(this, 1)