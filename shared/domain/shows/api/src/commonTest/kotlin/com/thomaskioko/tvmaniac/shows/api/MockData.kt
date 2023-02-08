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
    featuredCategoryState =  ShowResult.CategorySuccess(
        category = ShowCategory.FEATURED,
        tvShows = listOf(show)
    ),
    trendingCategoryState = ShowResult.CategorySuccess(
        category = ShowCategory.TRENDING,
        tvShows = listOf(show)
    ),
    popularCategoryState = ShowResult.CategorySuccess(
        category = ShowCategory.POPULAR,
        tvShows = listOf(show)
    ),
    anticipatedCategoryState = ShowResult.CategorySuccess(
        category = ShowCategory.ANTICIPATED,
        tvShows = listOf(show)
    )
)

val errorShowResult = ShowResult(
    featuredCategoryState = ShowResult.CategoryError(
        errorMessage = "Something went wrong"
    ),
    trendingCategoryState = ShowResult.CategoryError(
        errorMessage = "Something went wrong"
    ),
    popularCategoryState = ShowResult.CategoryError(
        errorMessage = "Something went wrong"
    ),
    anticipatedCategoryState =ShowResult.CategoryError(
        errorMessage = "Something went wrong"
    )
)

fun categoryResult(categoryId: Long) = listOf(
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
        category_id = categoryId,
    )
)