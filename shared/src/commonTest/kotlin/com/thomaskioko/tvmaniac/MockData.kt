package com.thomaskioko.tvmaniac

import com.thomaskioko.tvmaniac.datasource.cache.EpisodesBySeasonId
import com.thomaskioko.tvmaniac.datasource.cache.Season
import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.datasource.network.model.EpisodesResponse
import com.thomaskioko.tvmaniac.datasource.network.model.GenreResponse
import com.thomaskioko.tvmaniac.datasource.network.model.SeasonResponse
import com.thomaskioko.tvmaniac.datasource.network.model.SeasonsResponse
import com.thomaskioko.tvmaniac.datasource.network.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.datasource.network.model.ShowResponse
import com.thomaskioko.tvmaniac.datasource.network.model.TvShowsResponse
import com.thomaskioko.tvmaniac.datasource.repository.util.Resource
import com.thomaskioko.tvmaniac.presentation.contract.DiscoverShowResult
import com.thomaskioko.tvmaniac.presentation.model.EpisodeUiModel
import com.thomaskioko.tvmaniac.presentation.model.SeasonUiModel
import com.thomaskioko.tvmaniac.presentation.model.ShowUiModel
import kotlinx.coroutines.flow.flowOf
import com.thomaskioko.tvmaniac.datasource.cache.Episode as EpisodeCache

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
            overview = "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
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

    fun getSeasonsList() = listOf(
        SeasonUiModel(
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
            episodeCount = 1
        ),
        SeasonUiModel(
            seasonId = 77680,
            tvShowId = 84958,
            name = "Season 2",
            overview = "Strange things are afoot in Hawkins, Indiana, where a young boy's " +
                "sudden disappearance unearths a young girl with otherworldly powers.",
            seasonNumber = 1,
            episodeCount = 2
        )
    )

    fun getEpisodeResourceList() = flowOf(
        Resource.success(
            listOf(
                EpisodeUiModel(
                    id = 2534997,
                    seasonId = 114355,
                    name = "Glorious Purpose",
                    overview = "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
                    imageUrl = "https://image.tmdb.org/t/p/original/gxh0k3aADsYkt9tgkfm2kGn2qQj.jpg",
                    voteCount = 42,
                    voteAverage = 6.429,
                    seasonNumber = 1,
                    episodeNumber = "01"
                ),
                EpisodeUiModel(
                    id = 2927202,
                    seasonId = 114355,
                    name = "The Variant",
                    overview = "Mobius puts Loki to work, but not everyone at TVA is thrilled about the God of Mischief's presence.",
                    imageUrl = "https://image.tmdb.org/t/p/original/gqpcfkdmSsm6xiX2EsLkwUvA8g8.jpg",
                    voteCount = 23,
                    voteAverage = 7.6,
                    seasonNumber = 1,
                    episodeNumber = "02"
                )
            )
        )
    )

    fun getEpisodeList() = listOf(
        EpisodeUiModel(
            id = 2534997,
            seasonId = 114355,
            name = "Glorious Purpose",
            overview = "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
            imageUrl = "https://image.tmdb.org/t/p/original/gxh0k3aADsYkt9tgkfm2kGn2qQj.jpg",
            voteCount = 42,
            voteAverage = 6.429,
            seasonNumber = 1,
            episodeNumber = "01"
        ),
        EpisodeUiModel(
            id = 2927202,
            seasonId = 114355,
            name = "The Variant",
            overview = "Mobius puts Loki to work, but not everyone at TVA is thrilled about the God of Mischief's presence.",
            imageUrl = "https://image.tmdb.org/t/p/original/gqpcfkdmSsm6xiX2EsLkwUvA8g8.jpg",
            voteCount = 23,
            voteAverage = 7.6,
            seasonNumber = 1,
            episodeNumber = "02"
        )
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
        showUiModels = listOf(
            ShowUiModel(
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
        isLoading = false
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
                    season_ids = null,
                    status = "Ended",
                    popularity = 24.4848,
                    is_watchlist = false
                )
            )
        )
    )

    fun getSeasonCache(): Season {
        return Season(
            id = 4355,
            tv_show_id = 126280,
            name = "Season 1",
            overview = "A woman's daring sexual past collides with her married-with-kids " +
                "present when the bad-boy ex she can't stop fantasizing about crashes " +
                "back into her life.",
            season_number = 1,
            epiosode_count = 6,
            episode_ids = listOf(2534997, 2927202)
        )
    }

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

    fun getEpisodesBySeasonId() =
        flowOf(
            Resource.success(
                listOf(
                    EpisodesBySeasonId(
                        id = 2534997,
                        season_id = 114355,
                        name = "Glorious Purpose",
                        overview = "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
                        image_url = "https://image.tmdb.org/t/p/original/gxh0k3aADsYkt9tgkfm2kGn2qQj.jpg",
                        vote_count = 42,
                        vote_average = 6.429,
                        episode_number = "01",
                        episode_season_number = 1,
                        tv_show_id = 126280,
                        season_number = 1,
                        epiosode_count = 6,
                        episode_ids = listOf(2534997, 2927202),
                        id_ = null,
                        name_ = null,
                        overview_ = null
                    ),
                    EpisodesBySeasonId(
                        id = 2927202,
                        season_id = 114355,
                        name = "The Variant",
                        overview = "Mobius puts Loki to work, but not everyone at TVA is thrilled about the God of Mischief's presence.",
                        image_url = "https://image.tmdb.org/t/p/original/gqpcfkdmSsm6xiX2EsLkwUvA8g8.jpg",
                        vote_count = 23,
                        vote_average = 7.6,
                        season_number = 1,
                        episode_number = "02",
                        episode_season_number = 1,
                        tv_show_id = 126280,
                        epiosode_count = 6,
                        episode_ids = listOf(2534997, 2927202),
                        id_ = null,
                        name_ = null,
                        overview_ = null
                    )
                )
            )
        )

    fun getSelectSeasonsByShowId() = flowOf(
        Resource.success(
            listOf(
                SelectSeasonsByShowId(
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
                    title = "Loki",
                    poster_image_url = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                    backdrop_image_url = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                    language = "en",
                    votes = 4958,
                    vote_average = 8.1,
                    genre_ids = listOf(18, 10765),
                    epiosode_count = 1,
                    id_ = null,
                    description = null,
                    year = null,
                    season_ids = null,
                    episode_ids = null,
                    status = null,
                    popularity = null,
                    is_watchlist = false
                ),
                SelectSeasonsByShowId(
                    id = 77680,
                    tv_show_id = 84958,
                    name = "Season 2",
                    overview = "Strange things are afoot in Hawkins, Indiana, where a young boy's " +
                        "sudden disappearance unearths a young girl with otherworldly powers.",
                    season_number = 1,
                    epiosode_count = 2,
                    language = null,
                    votes = 4958,
                    vote_average = 8.1,
                    genre_ids = listOf(18, 10765),
                    id_ = null,
                    description = null,
                    year = null,
                    season_ids = null,
                    episode_ids = null,
                    title = null,
                    poster_image_url = null,
                    backdrop_image_url = null,
                    status = null,
                    popularity = null,
                    is_watchlist = false
                )
            )
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
        season_ids = null,
        status = "Ended",
        popularity = 24.4848,
        is_watchlist = true
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
            season_ids = null,
            status = "Ended",
            popularity = 24.4848,
            is_watchlist = true
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
            season_ids = null,
            status = "Ended",
            popularity = 24.4848,
            is_watchlist = false
        ),
    )
}
