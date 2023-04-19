package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.core.db.Episodes
import com.thomaskioko.tvmaniac.core.db.Seasons
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonEpisodesResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonsResponse

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

fun List<TraktSeasonsResponse>.toSeasonCacheList(traktId: Long): List<Seasons> =
    map { seasonResponse ->
        Seasons(
            show_trakt_id = traktId,
            id = seasonResponse.ids.trakt.toLong(),
            season_number = seasonResponse.number.toLong(),
            name = seasonResponse.title,
            overview = seasonResponse.overview,
            episode_count = seasonResponse.episodeCount.toLong(),
        )
    }
