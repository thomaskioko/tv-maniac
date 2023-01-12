package com.thomaskioko.tvmaniac.seasondetails.api

import com.thomaskioko.tvmaniac.seasondetails.api.model.Episode
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetails

val episodes = listOf(
    Episode(
        id = 12345,
        seasonId = 12343,
        episodeTitle = "Season 01",
        episodeNumberTitle = "E01 • Some title",
        overview = "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
        imageUrl = "https://image.tmdb.org/t/p/original/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        runtime = 45,
        voteCount = 4958,
        episodeNumber = "01",
        seasonEpisodeNumber = "S00 | E01"
    )
)

val seasonDetailsList = listOf(
    SeasonDetails(
        seasonId = 12343,
        seasonName = "Season 01",
        episodeCount = 1,
        watchProgress = 0.0f,
        episodes = episodes
    )
)

val seasonDetailsLoaded = SeasonDetailsLoaded(
    showTitle = "Loki",
    episodeList = seasonDetailsList
)
