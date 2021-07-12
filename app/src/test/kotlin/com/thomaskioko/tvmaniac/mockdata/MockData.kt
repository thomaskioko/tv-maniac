package com.thomaskioko.tvmaniac.mockdata

import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowsEntity

object MockData {

    fun makeTvShowEntityList() = listOf(
        TvShowsEntity(
            id = 1,
            showId = 84958,
            title = "Loki",
            description = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
                    "an alternate version of Loki is brought to the mysterious Time Variance " +
                    "Authority, a bureaucratic organization that exists outside of time and " +
                    "space and monitors the timeline. They give Loki a choice: face being " +
                    "erased from existence due to being a “time variant”or help fix " +
                    "the timeline and stop a greater threat.",
            imageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
            language = "en",
            votes = 4958,
            averageVotes = 8.1,
            genreIds = listOf(18, 10765),
        ),
        TvShowsEntity(
            id = 2,
            showId = 126280,
            title = "Sex/Life",
            description = "A woman's daring sexual past collides with her married-with-kids " +
                    "present when the bad-boy ex she can't stop fantasizing about crashes " +
                    "back into her life.",
            imageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
            language = "en",
            votes = 4958,
            averageVotes = 8.1,
            genreIds = listOf(35, 18),
        ),

    )
}