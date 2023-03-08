package com.thomaskioko.tvmaniac.seasondetails

import com.thomaskioko.tvmaniac.data.seasondetails.model.Episode
import com.thomaskioko.tvmaniac.data.seasondetails.model.SeasonDetails


val episode = Episode(
    id = 2534997,
    episodeNumberTitle = "E01 • Glorious Purpose",
    overview = "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
    voteCount = 42,
    runtime = 21,
    seasonId = 4654,
    imageUrl = "",
    episodeNumber = "01",
    episodeTitle = "Glorious Purpose",
    seasonEpisodeNumber = "S01 | E01"
)

val seasonsEpList: List<SeasonDetails> = listOf(
    SeasonDetails(
        seasonId = 1,
        seasonName = "Specials",
        episodeCount = 8,
        watchProgress = 0.4f,
        episodes = listOf(
            episode,
            episode,
            episode
        )
    )
)
