package com.thomaskioko.tvmaniac.details.implementation.mapper

import com.thomaskioko.tvmaniac.core.db.Last_episode
import com.thomaskioko.tvmaniac.core.db.SelectShows
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.util.DateUtil.formatDateString
import com.thomaskioko.tvmaniac.tmdb.api.model.LastEpisodeToAir
import com.thomaskioko.tvmaniac.tmdb.api.model.NextEpisodeToAir

fun List<SelectShows>.toShowList(): List<Show> {
    return map { it.toShow() }
}

fun SelectShows.toShow(): Show {
    return Show(
        id = id,
        title = title,
        description = description,
        language = language,
        poster_image_url = poster_image_url,
        backdrop_image_url = backdrop_image_url,
        votes = votes,
        vote_average = vote_average,
        genre_ids = genre_ids,
        year = year,
        status = status,
        popularity = popularity,
        number_of_episodes = number_of_episodes,
        number_of_seasons = number_of_seasons
    )
}

fun NextEpisodeToAir.toAirEp(tvShowId: Long) = Last_episode(
    id = id!!.toLong(),
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

fun LastEpisodeToAir.toAirEp(tvShowId: Long) = Last_episode(
    id = id!!.toLong(),
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
