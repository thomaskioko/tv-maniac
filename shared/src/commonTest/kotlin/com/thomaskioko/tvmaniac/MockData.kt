package com.thomaskioko.tvmaniac

import com.thomaskioko.tvmaniac.datasource.network.model.ShowResponse
import com.thomaskioko.tvmaniac.datasource.network.model.TvShowsResponse

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
                poster_Path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
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
                poster_Path = "/2ST6l4WP7ZfqAetuttBqx8F3AAH.jpg",
                voteAverage = 7.3,
                voteCount = 212,
            )
        ),
        totalPages = 100,
        totalResults = 5
    )
}