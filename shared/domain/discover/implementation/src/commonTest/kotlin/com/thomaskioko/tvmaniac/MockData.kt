package com.thomaskioko.tvmaniac

import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.discover.api.DiscoverShowResult
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory
import com.thomaskioko.tvmaniac.shows.api.model.TvShow
import kotlinx.coroutines.flow.flowOf

object MockData {

    fun getDiscoverShowResult(): DiscoverShowResult =
        DiscoverShowResult(
            featuredShows = getDiscoverShowsData(ShowCategory.FEATURED),
            trendingShows = getDiscoverShowsData(ShowCategory.TRENDING),
            popularShows = getDiscoverShowsData(ShowCategory.POPULAR),
            recommendedShows = getDiscoverShowsData(ShowCategory.RECOMMENDED),
            anticipatedShows = getDiscoverShowsData(ShowCategory.ANTICIPATED)
        )

    private fun getDiscoverShowsData(category: ShowCategory) = DiscoverShowResult.DiscoverShowsData(
        category = category,
        tvShows = listOf(
            TvShow(
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
                year = "2019",
                status = "Ended"
            ),
        ),
    )

    fun getShowsCache() = flowOf(
        Resource.success(
            listOf(
                Show(
                    trakt_id = 84958,
                    title = "Loki",
                    overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
                        "an alternate version of Loki is brought to the mysterious Time Variance " +
                        "Authority, a bureaucratic organization that exists outside of time and " +
                        "space and monitors the timeline. They give Loki a choice: face being " +
                        "erased from existence due to being a “time variant”or help fix " +
                        "the timeline and stop a greater threat.",
                    poster_image_url = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                    backdrop_image_url = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                    language = "en",
                    votes = 4958,
                    rating = 8.1,
                    genres = listOf("Horror", "Action"),
                    year = "2019",
                    status = "Ended",
                    aired_episodes = 54,
                    tmdb_id = 123,
                    runtime = 0
                )
            )
        )
    )
}
