package com.thomaskioko.tvmaniac.seasonepisodes.api

import com.thomaskioko.tvmaniac.core.db.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.util.FormatterUtil
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.seasonepisodes.api.model.Episode
import com.thomaskioko.tvmaniac.seasonepisodes.api.model.SeasonWithEpisodes
import com.thomaskioko.tvmaniac.shows.api.model.TvShow


fun Resource<SelectByShowId>.toTvShow(): TvShow {
    return data?.let {
        TvShow(
            traktId = it.trakt_id,
            tmdbId = it.tmdb_id,
            title = it.title,
            overview = it.overview,
            language = it.language,
            posterImageUrl = it.poster_url,
            backdropImageUrl = it.backdrop_url,
            votes = it.votes,
            rating = it.rating,
            genres = it.genres,
            year = it.year,
            status = it.status,
        )
    } ?: TvShow.EMPTY_SHOW
}

fun List<SelectSeasonWithEpisodes>.toSeasonWithEpisodes(): List<SeasonWithEpisodes> {
    return groupBy { it.name }.map { groupMap ->
        SeasonWithEpisodes(
            seasonId = groupMap.value.first().season_id,
            seasonName = groupMap.key,
            episodes = groupMap.value.map { it.toEpisode() },
            episodeCount = groupMap.value.size,
            watchProgress = 0f // TODO:: Fetch watch progress
        )
    }
}

fun SelectSeasonWithEpisodes.toEpisode(): Episode {
    return Episode(
        id = id,
        seasonId = season_id,
        episodeTitle = name,
        episodeNumberTitle = "E$episode_number • $title_",
        overview = overview__,
        imageUrl = image_url.toImageUrl(),
        runtime = runtime,
        voteCount = votes,
        episodeNumber = episode_number,
        seasonEpisodeNumber = "S${
        season_number
            .toString()
            .padStart(2, '0')
        } | E$episode_number"
    )
}

fun String?.toImageUrl() = FormatterUtil.formatPosterPath(this)
