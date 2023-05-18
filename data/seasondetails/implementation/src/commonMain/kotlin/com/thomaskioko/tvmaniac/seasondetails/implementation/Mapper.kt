package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.core.db.Episodes
import com.thomaskioko.tvmaniac.trakt.api.model.TraktEpisodesResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonEpisodesResponse

fun TraktSeasonEpisodesResponse.toEpisodeCacheList(): List<Episodes> {
    return episodes.map { episodeResponse ->
        Episodes(
            season_id = ids.trakt.toLong(),
            trakt_id = episodeResponse.ids.trakt.toLong(),
            tmdb_id = episodeResponse.ids.tmdb?.toLong(),
            title = episodeResponse.title,
            overview = episodeResponse.overview ?: "TBA",
            ratings = episodeResponse.ratings,
            runtime = episodeResponse.runtime.toLong(),
            votes = episodeResponse.votes.toLong(),
            episode_number = episodeResponse.episodeNumber.toString().padStart(2, '0'),
        )
    }
}

fun List<TraktEpisodesResponse>.toEpisodeCache(seasonId: Long): List<Episodes> {
    return map { episodeResponse ->
        Episodes(
            season_id = seasonId,
            trakt_id = episodeResponse.ids.trakt.toLong(),
            tmdb_id = episodeResponse.ids.tmdb?.toLong(),
            title = episodeResponse.title,
            overview = episodeResponse.overview ?: "TBA",
            ratings = episodeResponse.ratings,
            runtime = episodeResponse.runtime.toLong(),
            votes = episodeResponse.votes.toLong(),
            episode_number = episodeResponse.episodeNumber.toString().padStart(2, '0'),
        )
    }
}
