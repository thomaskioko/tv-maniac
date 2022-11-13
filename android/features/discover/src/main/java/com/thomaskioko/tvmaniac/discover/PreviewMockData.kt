package com.thomaskioko.tvmaniac.discover

import com.thomaskioko.tvmaniac.shows.api.ShowResult
import com.thomaskioko.tvmaniac.shows.api.ShowsLoaded
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory
import com.thomaskioko.tvmaniac.shows.api.model.TvShow

val shows = TvShow(
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


val showsLoaded = ShowsLoaded(
    result = ShowResult(
        featuredShows = ShowResult.ShowCategoryData(
            category = ShowCategory.FEATURED,
            tvShows = List(5) { shows }
        ),
        trendingShows = ShowResult.ShowCategoryData(
            category = ShowCategory.TRENDING,
            tvShows = List(10) { shows }
        ),
        popularShows = ShowResult.ShowCategoryData(
            category = ShowCategory.POPULAR,
            tvShows = List(10) { shows }
        ),
        anticipatedShows = ShowResult.ShowCategoryData(
            category = ShowCategory.POPULAR,
            tvShows = List(10) { shows }
        ),
    )
)

