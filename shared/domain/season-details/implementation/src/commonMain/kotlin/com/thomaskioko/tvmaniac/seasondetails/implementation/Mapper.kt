package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.core.db.Episode
import com.thomaskioko.tvmaniac.core.db.Season
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonEpisodesResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonsResponse

fun TraktSeasonEpisodesResponse.toEpisodeCacheList(): List<Episode> {
    return episodes.map { episodeResponse ->
        Episode(
            season_id = ids.trakt,
            id = episodeResponse.ids.trakt,
            tmdb_id = episodeResponse.ids.tmdb,
            title = episodeResponse.title,
            overview = episodeResponse.overview ?: "TBA",
            ratings = episodeResponse.ratings,
            runtime = episodeResponse.runtime,
            votes = episodeResponse.votes,
            episode_number = episodeResponse.episodeNumber.toString().padStart(2, '0'),
        )
    }
}

fun List<TraktSeasonsResponse>.toSeasonCacheList(traktId: Int): List<Season> =
    map { seasonResponse ->
        Season(
            show_trakt_id = traktId,
            id = seasonResponse.ids.trakt,
            season_number = seasonResponse.number,
            name = seasonResponse.title,
            overview = seasonResponse.overview,
            episode_count = seasonResponse.episodeCount
        )
    }

