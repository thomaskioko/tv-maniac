package com.thomaskioko.tvmaniac.core.db

import com.thomaskioko.tvmaniac.core.db.Episode as EpisodeCache

object MockData {

    fun getEpisodeCacheList() = listOf(
        EpisodeCache(
            id = 2534997,
            season_id = 114355,
            title = "Glorious Purpose",
            overview = "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
            votes = 42,
            ratings = 6.429,
            runtime = 45,
            episode_number = "01",
            tmdb_id = 1
        ),
        EpisodeCache(
            id = 2927202,
            season_id = 114355,
            title = "The Variant",
            overview = "Mobius puts Loki to work, but not everyone at TVA is thrilled about the God of Mischief's presence.",
            votes = 23,
            ratings = 7.6,
            runtime = 45,
            episode_number = "02",
            tmdb_id = 1
        )
    )

    fun getSeasonCacheList() = listOf(
        Season(
            id = 114355,
            show_id = 84958,
            name = "Season 1",
            overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
                "an alternate version of Loki is brought to the mysterious Time Variance " +
                "Authority, a bureaucratic organization that exists outside of time and " +
                "space and monitors the timeline. They give Loki a choice: face being " +
                "erased from existence due to being a “time variant”or help fix " +
                "the timeline and stop a greater threat.",
            season_number = 1,
            epiosode_count = 6,
        ),
        Season(
            id = 77680,
            show_id = 84958,
            name = "Season 2",
            overview = "Strange things are afoot in Hawkins, Indiana, where a young boy's " +
                "sudden disappearance unearths a young girl with otherworldly powers.",
            season_number = 1,
            epiosode_count = 4,
        ),
        Season(
            id = 4355,
            show_id = 126280,
            name = "Season 1",
            overview = "A woman's daring sexual past collides with her married-with-kids " +
                "present when the bad-boy ex she can't stop fantasizing about crashes " +
                "back into her life.",
            season_number = 1,
            epiosode_count = 6,
        )
    )

    fun getShow() = Show(
        trakt_id = 84958,
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
        genres = listOf("Horror"),
        year = "2019",
        status = "Ended",
        tmdb_id = 126280,
        runtime = 0,
        aired_episodes = 12
    )

    fun makeShowList() = listOf(
        Show(
            trakt_id = 84958,
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
            genres = listOf("Horror"),
            year = "2019",
            status = "Ended",
            tmdb_id = 126280,
            runtime = 0,
            aired_episodes = 12
        ),
        Show(
            trakt_id = 126280,
            title = "Sex/Life",
            overview = "A woman's daring sexual past collides with her married-with-kids " +
                "present when the bad-boy ex she can't stop fantasizing about crashes " +
                "back into her life.",
            language = "en",
            votes = 4958,
            rating = 8.1,
            genres = listOf("Horror"),
            year = "2019",
            status = "Ended",
            tmdb_id = 126280,
            runtime = 0,
            aired_episodes = 12
        ),
    )
}
