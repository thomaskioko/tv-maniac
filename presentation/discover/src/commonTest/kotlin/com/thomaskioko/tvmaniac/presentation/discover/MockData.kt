package com.thomaskioko.tvmaniac.presentation.discover

import com.thomaskioko.tvmaniac.core.db.ShowsByCategory
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.presentation.discover.model.DiscoverShow
import com.thomaskioko.tvmaniac.presentation.discover.model.TvShow
import kotlinx.collections.immutable.toImmutableList

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

val discoverShow = DiscoverShow(
    tmdbId = 849583,
    title = "Loki",
    posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
)

val discoverContent = DataLoaded(
    featuredShows = listOf(discoverShow).toImmutableList(),
    topRatedShows = listOf(discoverShow).toImmutableList(),
    popularShows = listOf(show).toImmutableList(),
    upcomingShows = listOf(discoverShow).toImmutableList(),
)

fun categoryResult(categoryId: Long) = listOf(
    ShowsByCategory(
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
        category_id = Id(categoryId),
        aired_episodes = null,
    ),
)

fun updateCategoryResult(categoryId: Long, size: Int = 1) = List(size) {
    ShowsByCategory(
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
        category_id = Id(categoryId),
        aired_episodes = null,
    )
}
