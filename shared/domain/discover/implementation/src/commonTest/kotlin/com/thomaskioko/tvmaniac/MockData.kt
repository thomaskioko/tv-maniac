package com.thomaskioko.tvmaniac

import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.discover.api.DiscoverShowResult
import com.thomaskioko.tvmaniac.tmdb.api.model.ShowResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TvShowsResponse
import com.thomaskioko.tvmaniac.showcommon.api.model.ShowCategory
import com.thomaskioko.tvmaniac.showcommon.api.model.TvShow
import kotlinx.coroutines.flow.flowOf

object MockData {

    fun getTvResponse() = TvShowsResponse(
        page = 1,
        results = listOf(
            ShowResponse(
                backdropPath = "/wr7nrzDrpGCEgYnw15jyAB59PtZ.jpg",
                firstAirDate = "2021-06-09",
                genreIds = listOf(18, 10765),
                id = 84958,
                name = "Loki",
                originCountry = listOf("US"),
                originalLanguage = "en",
                originalName = "Loki",
                overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
                    "an alternate version of Loki is brought to the mysterious Time Variance " +
                    "Authority, a bureaucratic organization that exists outside of time and " +
                    "space and monitors the timeline. They give Loki a choice: face being " +
                    "erased from existence due to being a “time variant”or help fix " +
                    "the timeline and stop a greater threat.",
                popularity = 6005.364,
                posterPath = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                voteAverage = 8.1,
                voteCount = 4958,
            ),
            ShowResponse(
                backdropPath = "/9nBVkNBe4x9HKDAzxjxlIqecxCW.jpg",
                firstAirDate = "2021-06-25",
                genreIds = listOf(35, 18),
                id = 126280,
                name = "Sex/Life",
                originCountry = listOf("US"),
                originalLanguage = "en",
                originalName = "Loki",
                overview = "A woman's daring sexual past collides with her married-with-kids " +
                    "present when the bad-boy ex she can't stop fantasizing about crashes " +
                    "back into her life.",
                popularity = 2275.168,
                posterPath = "/2ST6l4WP7ZfqAetuttBqx8F3AAH.jpg",
                voteAverage = 7.3,
                voteCount = 212,
            )
        ),
        totalPages = 100,
        totalResults = 5
    )

    fun getDiscoverShowResult(): DiscoverShowResult =
        DiscoverShowResult(
            featuredShows = getDiscoverShowsData(ShowCategory.TRENDING),
            trendingShows = getDiscoverShowsData(ShowCategory.TRENDING),
            popularShows = getDiscoverShowsData(ShowCategory.POPULAR),
            topRatedShows = getDiscoverShowsData(ShowCategory.TOP_RATED)
        )

    private fun getDiscoverShowsData(category: ShowCategory) = DiscoverShowResult.DiscoverShowsData(
        category = category,
        tvShows = listOf(
            TvShow(
                id = 84958,
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
                averageVotes = 8.1,
                genreIds = listOf(18, 10765),
                year = "2019",
                status = "Ended"
            ),
        ),
    )

    fun getShowsCache() = flowOf(
        Resource.success(
            listOf(
                Show(
                    id = 84958,
                    title = "Loki",
                    description = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
                        "an alternate version of Loki is brought to the mysterious Time Variance " +
                        "Authority, a bureaucratic organization that exists outside of time and " +
                        "space and monitors the timeline. They give Loki a choice: face being " +
                        "erased from existence due to being a “time variant”or help fix " +
                        "the timeline and stop a greater threat.",
                    poster_image_url = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                    backdrop_image_url = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                    language = "en",
                    votes = 4958,
                    vote_average = 8.1,
                    genre_ids = listOf(18, 10765),
                    year = "2019",
                    status = "Ended",
                    popularity = 24.4848,
                    number_of_episodes = 30,
                    number_of_seasons = 2
                )
            )
        )
    )
}
