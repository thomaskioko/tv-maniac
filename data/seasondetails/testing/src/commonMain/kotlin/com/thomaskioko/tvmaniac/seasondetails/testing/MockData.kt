package com.thomaskioko.tvmaniac.seasondetails.testing

import com.thomaskioko.tvmaniac.seasondetails.api.model.EpisodeDetails
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetailsWithEpisodes

val SeasonWithEpisodeList =
  SeasonDetailsWithEpisodes(
    seasonId = 12343,
    tvShowId = 84958,
    name = "Season 01",
    showTitle = "Loki",
    seasonNumber = 0,
    episodeCount = 1,
    imageUrl = "https://image.tmdb.org/t/p/w500/path/to/image.jpg",
    seasonOverview =
      "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
    episodes =
      listOf(
        EpisodeDetails(
          runtime = 45,
          overview =
            "The journey to reunite the Ingham family continues as they travel to the USA.",
          episodeNumber = 1,
          stillPath = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
          name = "Some title",
          seasonId = 12343,
          id = 12345,
          seasonNumber = 0,
          voteAverage = 8.0,
          voteCount = 4958,
          isWatched = false,
        ),
      ),
  )
