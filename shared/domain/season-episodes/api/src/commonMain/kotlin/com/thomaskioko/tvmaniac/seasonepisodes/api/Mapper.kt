package com.thomaskioko.tvmaniac.seasonepisodes.api

import com.thomaskioko.tvmaniac.core.db.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.seasonepisodes.api.model.Episode
import com.thomaskioko.tvmaniac.seasonepisodes.api.model.SeasonWithEpisodes
import com.thomaskioko.tvmaniac.showcommon.api.model.TvShow


fun Resource<Show>.toTvShow(): TvShow {
    return data?.let {
        TvShow(
            id = it.id,
            title = it.title,
            overview = it.description,
            language = it.language,
            posterImageUrl = it.poster_image_url,
            backdropImageUrl = it.backdrop_image_url,
            votes = it.votes.toInt(),
            averageVotes = it.vote_average,
            genreIds = it.genre_ids,
            year = it.year,
            status = it.status,
            following = it.following
        )
    } ?: TvShow.EMPTY_SHOW
}

fun Resource<List<SelectSeasonWithEpisodes>>.toSeasonWithEpisodes(): List<SeasonWithEpisodes> {
    return data?.groupBy { it.name }?.map { groupMap ->
        SeasonWithEpisodes(
            seasonName = groupMap.key,
            episodes = groupMap.value.map { it.toEpisode() },
            episodeCount = groupMap.value.size,
            watchProgress = 0f // TODO:: Fetch watch progress
        )
    } ?: emptyList()
}

fun SelectSeasonWithEpisodes.toEpisode(): Episode {
    return Episode(
        id = id,
        seasonId = season_id,
        episodeTitle = name_,
        episodeNumberTitle = "E$episode_number • $name_",
        overview = overview_,
        imageUrl = image_url,
        voteAverage = vote_average_,
        voteCount = vote_count.toInt(),
        episodeNumber = episode_number,
        seasonEpisodeNumber = "S${
        season_number
            .toString()
            .padStart(2, '0')
        } | E$episode_number"
    )
}
