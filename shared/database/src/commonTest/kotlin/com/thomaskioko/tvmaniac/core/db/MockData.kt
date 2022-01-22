package com.thomaskioko.tvmaniac.core.db

import com.thomaskioko.tvmaniac.datasource.cache.Last_episode
import com.thomaskioko.tvmaniac.datasource.cache.Season
import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.datasource.cache.Episode as EpisodeCache

object MockData {

    fun getEpisodeCacheList() = listOf(
        EpisodeCache(
            id = 2534997,
            season_id = 114355,
            name = "Glorious Purpose",
            overview = "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
            image_url = "https://image.tmdb.org/t/p/original/gxh0k3aADsYkt9tgkfm2kGn2qQj.jpg",
            vote_count = 42,
            vote_average = 6.429,
            episode_season_number = 1,
            episode_number = "01"
        ),
        EpisodeCache(
            id = 2927202,
            season_id = 114355,
            name = "The Variant",
            overview = "Mobius puts Loki to work, but not everyone at TVA is thrilled about the God of Mischief's presence.",
            image_url = "https://image.tmdb.org/t/p/original/gqpcfkdmSsm6xiX2EsLkwUvA8g8.jpg",
            vote_count = 23,
            vote_average = 7.6,
            episode_season_number = 1,
            episode_number = "02"
        )
    )

    fun getSeasonCacheList() = listOf(
        Season(
            id = 114355,
            tv_show_id = 84958,
            name = "Season 1",
            overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
                "an alternate version of Loki is brought to the mysterious Time Variance " +
                "Authority, a bureaucratic organization that exists outside of time and " +
                "space and monitors the timeline. They give Loki a choice: face being " +
                "erased from existence due to being a “time variant”or help fix " +
                "the timeline and stop a greater threat.",
            season_number = 1,
            epiosode_count = 6,
            episode_ids = null
        ),
        Season(
            id = 77680,
            tv_show_id = 84958,
            name = "Season 2",
            overview = "Strange things are afoot in Hawkins, Indiana, where a young boy's " +
                "sudden disappearance unearths a young girl with otherworldly powers.",
            season_number = 1,
            epiosode_count = 4,
            episode_ids = null
        ),
        Season(
            id = 4355,
            tv_show_id = 126280,
            name = "Season 1",
            overview = "A woman's daring sexual past collides with her married-with-kids " +
                "present when the bad-boy ex she can't stop fantasizing about crashes " +
                "back into her life.",
            season_number = 1,
            epiosode_count = 6,
            episode_ids = null
        )
    )

    fun getShow() = Show(
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
        following = true,
        number_of_seasons = 2,
        number_of_episodes = 12
    )

    fun makeShowList() = listOf(
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
            status = null,
            popularity = 24.4848,
            following = true,
            number_of_seasons = 2,
            number_of_episodes = 12
        ),
        Show(
            id = 126280,
            title = "Sex/Life",
            description = "A woman's daring sexual past collides with her married-with-kids " +
                "present when the bad-boy ex she can't stop fantasizing about crashes " +
                "back into her life.",
            poster_image_url = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
            backdrop_image_url = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
            language = "en",
            votes = 4958,
            vote_average = 8.1,
            genre_ids = listOf(35, 18),
            year = "2019",
            status = "Ended",
            popularity = 24.4848,
            following = false,
            number_of_seasons = 2,
            number_of_episodes = 12
        ),
    )

    fun makeLastEpisodeList(): List<Last_episode> = listOf(
        Last_episode(
            id = 126280,
            show_id = 84958,
            name = "Follow the Leader?",
            overview = "A woman's daring sexual past collides with her married-with-kids " +
                "present when the bad-boy ex she can't stop fantasizing about crashes " +
                "back into her life.",
            title = "Latest",
            air_date = "2014-03-28",
            episode_number = 3,
            season_number = 1,
            still_path = null,
            vote_count = 0,
            vote_average = 12.2
        ),
        Last_episode(
            id = 12628,
            show_id = 84958,
            name = "Follow the Leader?",
            overview = "A woman's daring sexual past collides with her married-with-kids " +
                "present when the bad-boy ex she can't stop fantasizing about crashes " +
                "back into her life.",
            title = "Coming soon",
            air_date = "2014-03-28",
            episode_number = 3,
            season_number = 1,
            still_path = null,
            vote_count = 0,
            vote_average = 12.2
        )
    )
}
