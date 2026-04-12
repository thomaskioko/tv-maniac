package com.thomaskioko.tvmaniac.seasondetails.presenter.data

import com.thomaskioko.tvmaniac.seasondetails.api.model.EpisodeDetails
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetailsWithEpisodes
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsModel
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.EpisodeDetailsModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal fun buildSeasonDetailsLoaded(
    episodeDetailsList: ImmutableList<EpisodeDetailsModel> = persistentListOf(),
): SeasonDetailsModel {
    return SeasonDetailsModel(
        seasonId = 12343L,
        seasonName = "Season 01",
        episodeCount = 1,
        watchProgress = 0.0f,
        episodeDetailsList = episodeDetailsList,
        seasonImages = persistentListOf(),
        seasonOverview =
        "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
        isSeasonWatched = false,
        imageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        seasonCast = persistentListOf(),
    )
}

internal fun buildSeasonDetailsWithEpisodes(
    seasonId: Long = 12343,
    showTraktId: Long = 84958,
    showTmdbId: Long = 84958,
    name: String = "Season 01",
    seasonOverview: String =
        "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
    episodeCount: Long = 0,
    imageUrl: String = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    episodes: List<EpisodeDetails> = emptyList(),
    seasonNumber: Long = 1,
    showTitle: String = "Loki",
): SeasonDetailsWithEpisodes {
    return SeasonDetailsWithEpisodes(
        seasonId = seasonId,
        name = name,
        seasonOverview = seasonOverview,
        episodeCount = episodeCount,
        imageUrl = imageUrl,
        episodes = episodes,
        seasonNumber = seasonNumber,
        showTitle = showTitle,
        showTraktId = showTraktId,
        showTmdbId = showTmdbId,
    )
}
