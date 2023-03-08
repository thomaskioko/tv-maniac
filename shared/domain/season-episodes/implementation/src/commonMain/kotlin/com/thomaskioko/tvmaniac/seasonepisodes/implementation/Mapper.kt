package com.thomaskioko.tvmaniac.seasonepisodes.implementation

import com.thomaskioko.tvmaniac.core.db.Episode
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonEpisodesResponse

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
