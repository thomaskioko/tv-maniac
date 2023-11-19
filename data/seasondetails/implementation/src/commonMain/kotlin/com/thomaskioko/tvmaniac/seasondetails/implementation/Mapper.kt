package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.core.db.Episode
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonEpisodesResponse

internal fun List<TraktSeasonEpisodesResponse>.toSeasonWithEpisodes(): List<SeasonData> {
    return map { season ->
        SeasonData(
            seasonId = season.ids.trakt.toLong(),
            title = season.title,
            overview = season.overview ?: "TBA",
            episodeCount = season.episodeCount.toLong(),
            seasonNumber = season.number.toLong(),
            episodes = season.toEpisodeCacheList(),
        )
    }
}

fun TraktSeasonEpisodesResponse.toEpisodeCacheList(): List<Episode> {
    return episodes.map { episodeResponse ->
        Episode(
            id = Id(episodeResponse.ids.trakt.toLong()),
            season_id = Id(ids.trakt.toLong()),
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

internal data class SeasonData(
    val seasonId: Long,
    val title: String,
    val overview: String,
    val seasonNumber: Long,
    val episodeCount: Long,
    val episodes: List<Episode>,
)
