package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.util.StringUtil.formatPosterPath
import com.thomaskioko.tvmaniac.tmdb.api.model.SeasonResponse
import com.thomaskioko.tvmaniac.core.db.Episode as EpisodeCache

fun SeasonResponse.toEpisodeCacheList(): List<EpisodeCache> {
    return episodes.map { episodeResponse ->
        EpisodeCache(
            id = episodeResponse.id.toLong(),
            season_id = id.toLong(),
            name = episodeResponse.name,
            overview = episodeResponse.overview,
            image_url = formatPosterPath(episodeResponse.still_path),
            vote_average = episodeResponse.vote_average,
            vote_count = episodeResponse.vote_count.toLong(),
            episode_number = episodeResponse.episode_number.toString().padStart(2, '0')
        )
    }
}
