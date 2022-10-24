package com.thomaskioko.tvmaniac.seasons

import com.thomaskioko.tvmaniac.seasonepisodes.api.model.Episode
import com.thomaskioko.tvmaniac.seasonepisodes.api.model.SeasonWithEpisodes
import com.thomaskioko.tvmaniac.seasons.api.model.SeasonUiModel

val seasons = listOf(
    SeasonUiModel(
        seasonId = 114355,
        tvShowId = 84958,
        name = "Season 1",
        overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
            "an alternate version of Loki is brought to the mysterious Time Variance " +
            "Authority, a bureaucratic organization that exists outside of time and " +
            "space and monitors the timeline. They give Loki a choice: face being " +
            "erased from existence due to being a “time variant”or help fix " +
            "the timeline and stop a greater threat.",
        seasonNumber = 1,
        episodeCount = 6
    )
)

val episode = Episode(
    id = 2534997,
    episodeNumberTitle = "E01 • Glorious Purpose",
    overview = "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
    voteCount = 42,
    voteAverage = 6.429,
    seasonId = 4654,
    imageUrl = "",
    episodeNumber = "01",
    episodeTitle = "Glorious Purpose",
    seasonEpisodeNumber = "S01 | E01"
)

val seasonsEpList: List<SeasonWithEpisodes> = listOf(
    SeasonWithEpisodes(
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
