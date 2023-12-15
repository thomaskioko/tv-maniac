package com.thomaskioko.tvmaniac.data.seasondetails

import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsLoaded
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.EpisodeDetailsModel
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonDetailsModel
import kotlinx.collections.immutable.persistentListOf

val episodeDetailModels = persistentListOf(
    EpisodeDetailsModel(
        id = 12345,
        seasonId = 12343,
        episodeTitle = "Some title",
        episodeNumberTitle = "E01 • Some title",
        overview = "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
        imageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        runtime = 45,
        voteCount = 4958,
        episodeNumber = "01",
        seasonEpisodeNumber = "S00 | E01",
    ),
)

val seasonDetailsLoaded = SeasonDetailsLoaded(
    showTitle = "Loki",
    seasonDetailsModel = SeasonDetailsModel(
        seasonId = 12343,
        seasonName = "Season 01",
        episodeCount = 1,
        watchProgress = 0.0f,
        episodeDetailModels = episodeDetailModels,
    ),
)
