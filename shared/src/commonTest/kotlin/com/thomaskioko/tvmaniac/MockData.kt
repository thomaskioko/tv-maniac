package com.thomaskioko.tvmaniac

import com.thomaskioko.tvmaniac.datasource.cache.model.EpisodeEntity
import com.thomaskioko.tvmaniac.datasource.cache.model.SeasonsEntity
import com.thomaskioko.tvmaniac.datasource.cache.model.TvShows
import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow
import com.thomaskioko.tvmaniac.datasource.enums.TrendingDataRequest
import com.thomaskioko.tvmaniac.datasource.enums.TvShowCategory
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShowEntity
import com.thomaskioko.tvmaniac.datasource.network.model.EpisodesResponse
import com.thomaskioko.tvmaniac.datasource.network.model.GenreResponse
import com.thomaskioko.tvmaniac.datasource.network.model.SeasonResponse
import com.thomaskioko.tvmaniac.datasource.network.model.SeasonsResponse
import com.thomaskioko.tvmaniac.datasource.network.model.ShowDetailResponse
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

    fun getShowDetailResponse() = ShowDetailResponse(
        id = 84958,
        backdropPath = "/wr7nrzDrpGCEgYnw15jyAB59PtZ.jpg",
        firstAirDate = "2021-06-09",
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
        voteAverage = 8.1,
        voteCount = 4958,
        seasons = getSeasonsListResponse(),
        languages = listOf("en"),
        numberOfEpisodes = 6,
        posterPath = "/x2LSRK2Cm7MZhjluni1msVJ3wDF.jpg",
        homepage = "https://www.netflix.com/title/80057281",
        lastAirDate = "2019-07-04",
        numberOfSeasons = 1,
        in_production = true,
        episodeRunTime = listOf(50),
        tagline = "It only gets stranger…",
        status = "Returning  Series",
        genres = getGenreResponse(),
    )

    private fun getGenreResponse() = listOf(
        GenreResponse(
            id = 12,
            name = "Mystery"
        )
    )

    private fun getSeasonsListResponse() = listOf(
        SeasonsResponse(
            id = 114355,
            name = "Season 1",
            overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” " +
                    "an alternate version of Loki is brought to the mysterious Time Variance " +
                    "Authority, a bureaucratic organization that exists outside of time and " +
                    "space and monitors the timeline. They give Loki a choice: face being " +
                    "erased from existence due to being a “time variant”or help fix " +
                    "the timeline and stop a greater threat.",
            seasonNumber = 1,
            episodeCount = 6,
            airDate = "2016-07-15",
            posterPath = "/zka9GTG4QQhLmN4NR18KEjxICtt.jpg"
        )
    )

    fun getShowSeasonsResponse() = SeasonResponse(
        air_date = "2021-06-09",
        name = "Season 1",
        overview = "",
        id = 114355,
        poster_path = "/8uVqe9ThcuYVNdh4O0kuijIWMLL.jpg",
        season_number = 1,
        episodes = getEpisodesResponse()
    )

    private fun getEpisodesResponse() = listOf(
        EpisodesResponse(
            id = 2534997,
            air_date = "2021-06-09",
            episode_number = 1,
            name = "Glorious Purpose",
            overview = "After stealing the Tesseract in \"Avengers: Endgame,\" Loki lands before the Time Variance Authority.",
            production_code = "",
            season_number = 1,
            still_path = "/gxh0k3aADsYkt9tgkfm2kGn2qQj.jpg",
            vote_average = 6.429,
            vote_count = 42,
        ),
        EpisodesResponse(
            id = 2927202,
            air_date = "2021-06-09",
            episode_number = 2,
            name = "The Variant",
            overview = "Mobius puts Loki to work, but not everyone at TVA is thrilled about the God of Mischief's presence.",
            still_path = "/gqpcfkdmSsm6xiX2EsLkwUvA8g8.jpg",
            production_code = "",
            season_number = 1,
            vote_average = 7.6,
            vote_count = 23,
        ),
    )

    fun makeTvShowEntityList() = listOf(
        TvShows(
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
            showCategory = TvShowCategory.POPULAR_TV_SHOWS
        ),
        TvShows(
            id = 126280,
            title = "Sex/Life",
            overview = "A woman's daring sexual past collides with her married-with-kids " +
                    "present when the bad-boy ex she can't stop fantasizing about crashes " +
                    "back into her life.",
            posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
            backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
            language = "en",
            votes = 4958,
            averageVotes = 8.1,
            genreIds = listOf(35, 18),
            showCategory = TvShowCategory.POPULAR_TV_SHOWS
        ),
    )

    val tvShowsEntity = TvShows(
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
        showCategory = TvShowCategory.POPULAR_TV_SHOWS
    )


    val tvSeasonsList = listOf(
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
        ),
        SeasonsEntity(
            seasonId = 77680,
            tvShowId = 84958,
            name = "Season 2",
            overview = "Strange things are afoot in Hawkins, Indiana, where a young boy's " +
                    "sudden disappearance unearths a young girl with otherworldly powers.",
            seasonNumber = 1,
            episodeCount = 4
        ),
        SeasonsEntity(
            seasonId = 4355,
            tvShowId = 126280,
            name = "Season 1",
            overview = "A woman's daring sexual past collides with her married-with-kids " +
                    "present when the bad-boy ex she can't stop fantasizing about crashes " +
                    "back into her life.",
            seasonNumber = 1,
            episodeCount = 6
        )
    )

    val tvShowSeasonEntity = TvShows(
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
        showCategory = TvShowCategory.POPULAR_TV_SHOWS,
        seasonsList = tvSeasonsList
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

    fun getTrendingDataMap(): LinkedHashMap<TrendingDataRequest, List<TvShows>> {
        val trendingMap = linkedMapOf<TrendingDataRequest, List<TvShows>>()

        trendingMap[TrendingDataRequest.TODAY] = getTvResponse().results
            .map { it.toTvShowEntity() }
            .map { it.copy(
                showCategory = TvShowCategory.TRENDING,
                timeWindow = TimeWindow.DAY
            ) }

        trendingMap[TrendingDataRequest.THIS_WEEK] = getTvResponse().results
            .map { it.toTvShowEntity() }
            .map { it.copy(
                showCategory = TvShowCategory.TRENDING,
                timeWindow = TimeWindow.WEEK
            ) }

        return trendingMap
    }

}