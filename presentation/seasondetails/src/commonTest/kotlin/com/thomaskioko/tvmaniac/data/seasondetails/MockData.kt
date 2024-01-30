package com.thomaskioko.tvmaniac.data.seasondetails

import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsContent
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.EpisodeDetailsModel
import kotlinx.collections.immutable.persistentListOf

val episodeDetailModels = persistentListOf(
    EpisodeDetailsModel(
        id = 12345,
        seasonId = 12343,
        episodeTitle = "Some title",
        episodeNumberTitle = "E1 • Some title",
        overview = "The journey to reunite the Ingham family continues as they travel to the USA.",
        imageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        runtime = 45,
        voteCount = 4958,
        episodeNumber = "1",
        seasonEpisodeNumber = "S00 | E1",
    ),
)

val seasonDetailsContent = SeasonDetailsContent(
    seasonId = 12343,
    seasonName = "Season 01",
    episodeCount = 1,
    watchProgress = 0.0f,
    episodeDetailsList = episodeDetailModels,
    seasonImages = persistentListOf(),
    seasonOverview = "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
    isSeasonWatched = false,
    imageUrl = "https://image.tmdb.org/t/p/w500/path/to/image.jpg",
    seasonCast = persistentListOf(),
)
