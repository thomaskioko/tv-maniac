package com.thomaskioko.showdetails

import com.thomaskioko.tvmaniac.details.api.FollowShowsState
import com.thomaskioko.tvmaniac.details.api.SeasonState
import com.thomaskioko.tvmaniac.details.api.ShowDetailsState
import com.thomaskioko.tvmaniac.details.api.SimilarShowsState
import com.thomaskioko.tvmaniac.details.api.TrailersState
import com.thomaskioko.tvmaniac.details.api.model.Season
import com.thomaskioko.tvmaniac.details.api.model.Show
import com.thomaskioko.tvmaniac.details.api.model.Trailer

val detailUiState = ShowDetailsState.ShowDetailsLoaded(
    show = Show(
        traktId = 84958,
        title = "Loki",
        overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
                "an alternate version of Loki is brought to the mysterious Time Variance " +
                "Authority, a bureaucratic organization that exists outside of time and " +
                "space and monitors the timeline. They give Loki a choice: face being " +
                "erased from existence due to being a “time variant”or help fix " +
                "the timeline and stop a greater threat.",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        language = "en",
        votes = 4958,
        rating = 8.1,
        genres = listOf("Horror", "Action"),
        status = "Returning Series",
        year = "2024"
    ),
    seasonState = SeasonState.SeasonsLoaded(
        isLoading = false,
        seasonsList = getTvSeasons(),
    ),
    trailerState = TrailersState.TrailersLoaded(
        isLoading = true,
        trailersList = listOf(
            Trailer(
                showId = 1232,
                key = "",
                name = "",
                youtubeThumbnailUrl = ""
            ),
            Trailer(
                showId = 1232,
                key = "",
                name = "",
                youtubeThumbnailUrl = ""
            ),
        )
    ),
    similarShowsState = SimilarShowsState.SimilarShowsLoaded(
        isLoading = false,
        similarShows = emptyList()
    ),
    followShowState = FollowShowsState.Idle
)

private fun getTvSeasons() = listOf(
    Season(
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

val showList = listOf(
    Show(
        traktId = 84958,
        title = "Loki",
        overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
                "an alternate version of Loki is brought to the mysterious Time Variance " +
                "Authority, a bureaucratic organization that exists outside of time and " +
                "space and monitors the timeline. They give Loki a choice: face being " +
                "erased from existence due to being a “time variant”or help fix " +
                "the timeline and stop a greater threat.",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        language = "en",
        votes = 4958,
        rating = 8.1,
        genres = listOf("Horror", "Action"),
        status = "Returning Series",
        year = "2024"
    ),
    Show(
        traktId = 84958,
        title = "Loki",
        overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
                "an alternate version of Loki is brought to the mysterious Time Variance " +
                "Authority, a bureaucratic organization that exists outside of time and " +
                "space and monitors the timeline. They give Loki a choice: face being " +
                "erased from existence due to being a “time variant”or help fix " +
                "the timeline and stop a greater threat.",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        language = "en",
        votes = 4958,
        rating = 8.1,
        genres = listOf("Horror", "Action"),
        status = "Returning Series",
        year = "2024"
    )
)
