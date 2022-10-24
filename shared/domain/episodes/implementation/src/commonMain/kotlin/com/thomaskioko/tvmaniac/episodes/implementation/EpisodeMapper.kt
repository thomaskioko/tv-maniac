package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.util.FormatterUtil.formatPosterPath
import com.thomaskioko.tvmaniac.tmdb.api.model.SeasonResponse
import com.thomaskioko.tvmaniac.core.db.Episode as EpisodeCache

fun SeasonResponse.toEpisodeCacheList(): List<EpisodeCache> {
    return episodes.map { episodeResponse ->
        EpisodeCache(
            season_id = id,
            id = episodeResponse.id,
            tmdb_id = episodeResponse.id,
            title = episodeResponse.name,
            overview = episodeResponse.overview,
            image_url = formatPosterPath(episodeResponse.still_path),
            vote_average = episodeResponse.vote_average,
            votes = episodeResponse.vote_count,
            episode_number = episodeResponse.episode_number.toString().padStart(2, '0')
        )
    }
}
