package com.thomaskioko.tvmaniac.mockdata

import com.thomaskioko.tvmaniac.datasource.cache.model.EpisodeEntity
import com.thomaskioko.tvmaniac.datasource.cache.model.SeasonsEntity
import com.thomaskioko.tvmaniac.datasource.enums.TvShowCategory
import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowsEntity

object MockData {

    fun makeTvShowEntityList() = listOf(
        TvShowsEntity(
            id = 84958,
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
            showCategory = TvShowCategory.POPULAR_TV_SHOWS,
            seasonsList = tvSeasonList()
        ),
        TvShowsEntity(
            id = 126280,
            title = "Sex/Life",
            description = "A woman's daring sexual past collides with her married-with-kids " +
                    "present when the bad-boy ex she can't stop fantasizing about crashes " +
                    "back into her life.",
            imageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
            language = "en",
            votes = 4958,
            averageVotes = 8.1,
            genreIds = listOf(35, 18),
            showCategory = TvShowCategory.POPULAR_TV_SHOWS
        ),

        )

    fun tvSeasonList() = listOf(
        SeasonsEntity(
            seasonId = 114355,
            tvShowId = 84958,
            name = "Season 1",
            overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
                    "an alternate version of Loki is brought to the mysterious Time Variance " +
                    "Authority, a bureaucratic organization that exists outside of time and " +
                    "space and monitors the timeline. They give Loki a choice: face being " +
                    "erased from existence due to being a “time variant”or help fix " +
                    "the timeline and stop a greater threat.",
            seasonNumber = 1,
            episodeCount = 6
        )
    )

    fun getEpisodeEntityList() = listOf(
        EpisodeEntity(
            id = 2534997,
            seasonId = 114355,
            name = "Glorious Purpose",
            overview = "After stealing the Tesseract in \"Avengers: Endgame,\" Loki lands before the Time Variance Authority.",
            imageUrl = "/gxh0k3aADsYkt9tgkfm2kGn2qQj.jpg",
            voteCount = 42,
            voteAverage = 6.429,
            seasonNumber = 1,
            episodeNumber = 1
        ),
        EpisodeEntity(
            id = 2927202,
            seasonId = 114355,
            name = "The Variant",
            overview = "Mobius puts Loki to work, but not everyone at TVA is thrilled about the God of Mischief's presence.",
            imageUrl = "/gqpcfkdmSsm6xiX2EsLkwUvA8g8.jpg",
            voteCount = 23,
            voteAverage = 7.6,
            seasonNumber = 1,
            episodeNumber = 2
        )
    )

}