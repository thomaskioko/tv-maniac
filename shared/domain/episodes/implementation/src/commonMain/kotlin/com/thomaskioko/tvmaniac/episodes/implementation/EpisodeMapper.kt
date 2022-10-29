package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonEpisodesResponse
import com.thomaskioko.tvmaniac.core.db.Episode as EpisodeCache

fun List<TraktSeasonEpisodesResponse>.toEpisodeCacheList(): List<EpisodeCache> {
    return flatMap { seasonResponse ->
        seasonResponse.episodes.map { episodeResponse ->
            EpisodeCache(
                season_id = seasonResponse.ids.trakt,
                id = seasonResponse.ids.trakt,
                tmdb_id = episodeResponse.ids.tmdb,
                title = episodeResponse.title,
                overview = episodeResponse.overview ?: "",
                runtime = episodeResponse.runtime,
                votes = episodeResponse.votes,
                ratings = episodeResponse.ratings,
                episode_number = episodeResponse.episodeNumber.toString().padStart(2, '0')
            )
        }
    }
}
