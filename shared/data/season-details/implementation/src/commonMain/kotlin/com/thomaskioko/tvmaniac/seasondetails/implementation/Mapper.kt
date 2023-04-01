package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.core.db.Episode
import com.thomaskioko.tvmaniac.core.db.Season
import com.thomaskioko.tvmaniac.trakt.service.api.model.TraktSeasonEpisodesResponse
import com.thomaskioko.tvmaniac.trakt.service.api.model.TraktSeasonsResponse

fun TraktSeasonEpisodesResponse.toEpisodeCacheList(): List<Episode> {
    return episodes.map { episodeResponse ->
        Episode(
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

fun List<TraktSeasonsResponse>.toSeasonCacheList(traktId: Long): List<Season> =
    map { seasonResponse ->
        Season(
            show_trakt_id = traktId,
            id = seasonResponse.ids.trakt.toLong(),
            season_number = seasonResponse.number.toLong(),
            name = seasonResponse.title,
            overview = seasonResponse.overview,
            episode_count = seasonResponse.episodeCount.toLong()
        )
    }

