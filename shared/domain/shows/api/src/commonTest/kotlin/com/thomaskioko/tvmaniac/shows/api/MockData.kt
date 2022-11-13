package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory
import com.thomaskioko.tvmaniac.shows.api.model.TvShow

val show = TvShow(
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
    posterImageUrl = "https://image.tmdb.org/t/p/original/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    backdropImageUrl = "https://image.tmdb.org/t/p/original/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
)

val showResult = ShowResult(
    featuredShows = ShowResult.ShowCategoryData(
        category = ShowCategory.FEATURED,
        tvShows = listOf(show)
    ),
    trendingShows = ShowResult.ShowCategoryData(
        category = ShowCategory.TRENDING,
        tvShows = listOf(show)
    ),
    popularShows = ShowResult.ShowCategoryData(
        category = ShowCategory.POPULAR,
        tvShows = listOf(show)
    ),
    anticipatedShows = ShowResult.ShowCategoryData(
        category = ShowCategory.ANTICIPATED,
        tvShows = listOf(show)
    )
)