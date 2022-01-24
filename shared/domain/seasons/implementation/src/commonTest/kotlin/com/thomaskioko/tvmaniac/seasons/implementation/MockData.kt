package com.thomaskioko.tvmaniac.seasons.implementation

import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.remote.api.model.GenreResponse
import com.thomaskioko.tvmaniac.remote.api.model.SeasonsResponse
import com.thomaskioko.tvmaniac.remote.api.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.seasons.api.model.SeasonUiModel
import com.thomaskioko.tvmaniac.shared.core.util.Resource
import kotlinx.coroutines.flow.flowOf

internal object MockData {

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
        lastEpisodeToAir = null,
        nextEpisodeToAir = null
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
                    episode_ids = null,
                    status = null,
                    popularity = null,
                    following = false,
                    number_of_seasons = 2,
                    number_of_episodes = 21
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
                    episode_ids = null,
                    title = null,
                    poster_image_url = null,
                    backdrop_image_url = null,
                    status = null,
                    popularity = null,
                    following = false,
                    number_of_seasons = 2,
                    number_of_episodes = 21
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
        status = "Ended",
        popularity = 24.4848,
        following = true,
        number_of_seasons = 2,
        number_of_episodes = 21
    )
}
