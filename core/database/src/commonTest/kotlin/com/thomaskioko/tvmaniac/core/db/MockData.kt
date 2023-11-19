package com.thomaskioko.tvmaniac.core.db

import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.core.db.Episode as EpisodeCache

object MockData {

    fun getEpisodeCacheList() = listOf(
        EpisodeCache(
            id = Id(2534997),
            season_id = Id(114355),
            title = "Glorious Purpose",
            overview = "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
            votes = 42,
            ratings = 6.429,
            runtime = 45,
            episode_number = "01",
            tmdb_id = 1,
        ),
        EpisodeCache(
            id = Id(2927202),
            season_id = Id(114355),
            title = "The Variant",
            overview = "Mobius puts Loki to work, but not everyone at TVA is thrilled about the God of Mischief's presence.",
            votes = 23,
            ratings = 7.6,
            runtime = 45,
            episode_number = "02",
            tmdb_id = 1,
        ),
    )

    fun getShow() = Show(
        id = Id(84958),
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
        aired_episodes = 12,
    )

    fun showList() = listOf(
        Show(
            id = Id(84958),
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
            aired_episodes = 12,
        ),
        Show(
            id = Id(126280),
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
            aired_episodes = 12,
        ),
    )

    fun showCategory(traktId: Long, categoryId: Long) = Show_category(
        id = Id(traktId),
        category_id = Id(categoryId),
    )
}
