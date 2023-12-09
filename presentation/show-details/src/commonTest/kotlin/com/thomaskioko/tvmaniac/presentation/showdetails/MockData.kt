package com.thomaskioko.tvmaniac.presentation.showdetails

import com.thomaskioko.tvmaniac.core.db.SeasonsByShowId
import com.thomaskioko.tvmaniac.core.db.ShowById
import com.thomaskioko.tvmaniac.core.db.SimilarShows
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Season
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Show
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Trailer
import kotlinx.collections.immutable.persistentListOf

val show = Show(
    traktId = 84958,
    tmdbId = 849583,
    title = "Loki",
    overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
        "an alternate version of Loki is brought to the mysterious Time Variance " +
        "Authority, a bureaucratic organization that exists outside of time and " +
        "space and monitors the timeline. They give Loki a choice: face being " +
        "erased from existence due to being a “time variant”or help fix " +
        "the timeline and stop a greater threat.",
    language = "en",
    votes = 4958,
    rating = 8.1,
    genres = persistentListOf("Horror", "Action"),
    status = "Returning Series",
    year = "2024",
    posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
)

val similarShows = persistentListOf(
    Show(
        traktId = 184958,
        tmdbId = 284958,
        title = "Loki",
        overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
            "an alternate version of Loki is brought to the mysterious Time Variance " +
            "Authority, a bureaucratic organization that exists outside of time and " +
            "space and monitors the timeline. They give Loki a choice: face being " +
            "erased from existence due to being a “time variant”or help fix " +
            "the timeline and stop a greater threat.",
        language = "en",
        votes = 4958,
        rating = 8.1,
        genres = persistentListOf("Horror", "Action"),
        status = "Returning Series",
        year = "2024",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    ),
)

val showDetailsLoaded = ShowDetailsState(
    show = show,
    seasonsContent = ShowDetailsState.SeasonsContent(
        isLoading = true,
        seasonsList = persistentListOf(),
    ),
    similarShowsContent = ShowDetailsState.SimilarShowsContent(
        isLoading = true,
        similarShows = persistentListOf(),
    ),
    trailersContent = ShowDetailsState.TrailersContent(
        isLoading = true,
        hasWebViewInstalled = false,
        playerErrorMessage = null,
        trailersList = persistentListOf(),
    ),
    errorMessage = null,
)
val seasonsShowDetailsLoaded = ShowDetailsState.SeasonsContent(
    isLoading = false,
    seasonsList = persistentListOf(
        Season(
            seasonId = 84958,
            tvShowId = 114355,
            name = "Season 1",
        ),
    ),
)

val trailerShowDetailsLoaded = ShowDetailsState.TrailersContent(
    isLoading = false,
    hasWebViewInstalled = false,
    playerErrorMessage = null,
    trailersList = persistentListOf(
        Trailer(
            showId = 84958,
            key = "Fd43V",
            name = "Some title",
            youtubeThumbnailUrl = "https://i.ytimg.com/vi/Fd43V/hqdefault.jpg",
        ),
    ),
)

val similarShowLoaded = ShowDetailsState.SimilarShowsContent(
    isLoading = false,
    similarShows = similarShows,
)

val selectedShow = ShowById(
    id = Id(84958),
    tmdb_id = 849583,
    title = "Loki",
    overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
        "an alternate version of Loki is brought to the mysterious Time Variance " +
        "Authority, a bureaucratic organization that exists outside of time and " +
        "space and monitors the timeline. They give Loki a choice: face being " +
        "erased from existence due to being a “time variant”or help fix " +
        "the timeline and stop a greater threat.",
    language = "en",
    votes = 4958,
    rating = 8.1,
    genres = listOf("Horror", "Action"),
    status = "Returning Series",
    year = "2024",
    runtime = 45,
    poster_url = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    backdrop_url = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    aired_episodes = 12,
    in_watchlist = 0,
)
val similarShowResult = listOf(
    SimilarShows(
        id = Id(184958),
        tmdb_id = 284958,
        title = "Loki",
        overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
            "an alternate version of Loki is brought to the mysterious Time Variance " +
            "Authority, a bureaucratic organization that exists outside of time and " +
            "space and monitors the timeline. They give Loki a choice: face being " +
            "erased from existence due to being a “time variant”or help fix " +
            "the timeline and stop a greater threat.",
        language = "en",
        votes = 4958,
        rating = 8.1,
        genres = listOf("Horror", "Action"),
        status = "Returning Series",
        year = "2024",
        runtime = 45,
        poster_url = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        backdrop_url = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    ),
)

val seasons = listOf(
    SeasonsByShowId(
        season_id = Id(84958),
        show_id = Id(114355),
        season_title = "Season 1",
        season_number = 1,
    ),
)
