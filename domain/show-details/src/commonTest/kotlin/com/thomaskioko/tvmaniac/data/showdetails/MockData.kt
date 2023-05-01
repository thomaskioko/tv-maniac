package com.thomaskioko.tvmaniac.data.showdetails

import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.db.SelectSimilarShows
import com.thomaskioko.tvmaniac.domain.showdetails.FollowShowsState
import com.thomaskioko.tvmaniac.domain.showdetails.SeasonState
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsState
import com.thomaskioko.tvmaniac.domain.showdetails.ShowState
import com.thomaskioko.tvmaniac.domain.showdetails.SimilarShowsState
import com.thomaskioko.tvmaniac.domain.showdetails.TrailersState
import com.thomaskioko.tvmaniac.domain.showdetails.model.Season
import com.thomaskioko.tvmaniac.domain.showdetails.model.Show
import com.thomaskioko.tvmaniac.domain.showdetails.model.Trailer
import com.thomaskioko.tvmaniac.core.db.Seasons as SeasonCache

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
    genres = listOf("Horror", "Action"),
    status = "Returning Series",
    year = "2024",
    posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
)

val similarShows = listOf(
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
        genres = listOf("Horror", "Action"),
        status = "Returning Series",
        year = "2024",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    ),
)

val showDetailsLoaded = ShowDetailsState.ShowDetailsLoaded(
    showState = ShowState.ShowLoaded(
        show = show,
    ),
    seasonState = SeasonState.SeasonsLoaded(
        isLoading = true,
        seasonsList = emptyList(),
    ),
    similarShowsState = SimilarShowsState.SimilarShowsLoaded(
        isLoading = true,
        similarShows = emptyList(),
    ),
    trailerState = TrailersState.TrailersLoaded(
        isLoading = true,
        hasWebViewInstalled = false,
        playerErrorMessage = null,
        trailersList = emptyList(),
    ),
    followShowState = FollowShowsState.Idle,
)
val seasonsShowDetailsLoaded = SeasonState.SeasonsLoaded(
    isLoading = false,
    seasonsList = listOf(
        Season(
            seasonId = 84958,
            tvShowId = 114355,
            name = "Season 1",
        ),
    ),
)

val trailerShowDetailsLoaded = TrailersState.TrailersLoaded(
    isLoading = false,
    hasWebViewInstalled = false,
    playerErrorMessage = null,
    trailersList = listOf(
        Trailer(
            showId = 84958,
            key = "Fd43V",
            name = "Some title",
            youtubeThumbnailUrl = "https://i.ytimg.com/vi/Fd43V/hqdefault.jpg",
        ),
    ),
)

val similarShowLoaded = SimilarShowsState.SimilarShowsLoaded(
    isLoading = false,
    similarShows = similarShows,
)

val selectedShow = SelectByShowId(
    trakt_id = 84958,
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
    trakt_id_ = 1234,
    id = 12345,
    created_at = null,
    synced = false,
    tmdb_id_ = 1232,
)
val similarShowResult = listOf(
    SelectSimilarShows(
        trakt_id = 184958,
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
    SeasonCache(
        id = 84958,
        show_trakt_id = 114355,
        name = "Season 1",
        episode_count = 10,
        season_number = 1,
        overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
            "an alternate version of Loki is brought to the mysterious Time Variance " +
            "Authority, a bureaucratic organization that exists outside of time and " +
            "space and monitors the timeline. They give Loki a choice: face being " +
            "erased from existence due to being a “time variant”or help fix " +
            "the timeline and stop a greater threat.",
    ),
)
