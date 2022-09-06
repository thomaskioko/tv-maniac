package com.thomaskioko.tvmaniac.details.implementation.mapper

import com.thomaskioko.tvmaniac.core.db.Last_episode
import com.thomaskioko.tvmaniac.core.db.SelectShows
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.util.DateUtil.formatDateString
import com.thomaskioko.tvmaniac.core.util.FormatterUtil
import com.thomaskioko.tvmaniac.tmdb.api.model.LastEpisodeToAir
import com.thomaskioko.tvmaniac.tmdb.api.model.NextEpisodeToAir

fun List<SelectShows>.toShowList(): List<Show> {
    return map { it.toShow() }
}

fun SelectShows.toShow(): Show {
    return Show(
        trakt_id = trakt_id,
        tmdb_id = tmdb_id,
        title = title,
        overview = overview,
        language = language,
        votes = votes,
        rating = rating,
        genres = genres,
        year = year,
        status = status,
        aired_episodes = aired_episodes,
        runtime = runtime,
        poster_image_url = poster_image_url.toImageUrl(),
        backdrop_image_url = backdrop_image_url.toImageUrl()
    )
}



fun NextEpisodeToAir.toAirEp(tvShowId: Int) = Last_episode(
    id = id!!,
    show_id = tvShowId,
    name = name,
    overview = if (!overview.isNullOrEmpty()) overview!! else "TBA",
    air_date = formatDateString(dateString = airDate),
    episode_number = episodeNumber!!.toLong(),
    season_number = seasonNumber!!.toLong(),
    still_path = stillPath,
    vote_average = voteAverage,
    vote_count = voteCount?.toLong(),
    title = "Upcoming"
)

fun LastEpisodeToAir.toAirEp(tvShowId: Int) = Last_episode(
    id = id!!,
    show_id = tvShowId,
    name = name,
    overview = if (!overview.isNullOrEmpty()) overview!! else "TBA",
    air_date = formatDateString(dateString = airDate),
    episode_number = episodeNumber!!.toLong(),
    season_number = seasonNumber!!.toLong(),
    still_path = stillPath,
    vote_average = voteAverage,
    vote_count = voteCount?.toLong(),
    title = "Latest Release"
)

fun String?.toImageUrl() = FormatterUtil.formatPosterPath(this)

fun String?.toTmdbImageUrl(posterPath: String?) =
    if (this.isNullOrEmpty()) FormatterUtil.formatPosterPath(posterPath)
    else FormatterUtil.formatPosterPath(this)