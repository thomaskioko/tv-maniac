package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
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
    posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
)

val showResult = ShowResult(
    featuredShows = ShowResult.ShowCategoryData(
        categoryState = ShowResult.CategorySuccess(
            category = ShowCategory.FEATURED,
            tvShows = listOf(show)
        )
    ),
    trendingShows = ShowResult.ShowCategoryData(
        categoryState = ShowResult.CategorySuccess(
            category = ShowCategory.TRENDING,
            tvShows = listOf(show)
        )
    ),
    popularShows = ShowResult.ShowCategoryData(
        categoryState = ShowResult.CategorySuccess(
            category = ShowCategory.POPULAR,
            tvShows = listOf(show)
        )
    ),
    anticipatedShows = ShowResult.ShowCategoryData(
        categoryState = ShowResult.CategorySuccess(
            category = ShowCategory.ANTICIPATED,
            tvShows = listOf(show)
        )
    )
)

val emptyShowResult = ShowResult(
    featuredShows = ShowResult.ShowCategoryData(
        categoryState = ShowResult.CategoryError(
            category = ShowCategory.FEATURED,
            errorMessage = "Something went wrong"
        )
    ),
    trendingShows = ShowResult.ShowCategoryData(
        categoryState = ShowResult.CategoryError(
            category = ShowCategory.TRENDING,
            errorMessage = "Something went wrong"
        )
    ),
    popularShows = ShowResult.ShowCategoryData(
        categoryState = ShowResult.CategoryError(
            category = ShowCategory.POPULAR,
            errorMessage = "Something went wrong"
        )
    ),
    anticipatedShows = ShowResult.ShowCategoryData(
        categoryState = ShowResult.CategoryError(
            category = ShowCategory.ANTICIPATED,
            errorMessage = "Something went wrong"
        )
    )
)

val categoryResult = listOf(
    SelectShowsByCategory(
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
        category_id = 1,
        trakt_id__ = 12345,
        tmdb_id_ = 1232
    )
)