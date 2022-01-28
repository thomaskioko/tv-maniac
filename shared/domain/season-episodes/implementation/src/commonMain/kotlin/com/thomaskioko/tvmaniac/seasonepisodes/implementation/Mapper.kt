package com.thomaskioko.tvmaniac.seasonepisodes.implementation

import com.thomaskioko.tvmaniac.datasource.cache.Episode
import com.thomaskioko.tvmaniac.remote.api.model.SeasonResponse
import com.thomaskioko.tvmaniac.shared.core.util.StringUtil

fun SeasonResponse.toEpisodeCacheList(): List<Episode> {
    return episodes.map { episodeResponse ->
        Episode(
            id = episodeResponse.id.toLong(),
            season_id = id.toLong(),
            name = episodeResponse.name,
            overview = episodeResponse.overview,
            image_url = StringUtil.formatPosterPath(episodeResponse.still_path),
            vote_average = episodeResponse.vote_average,
            vote_count = episodeResponse.vote_count.toLong(),
            episode_number = episodeResponse.episode_number.toString().padStart(2, '0'),
        )
    }
}
