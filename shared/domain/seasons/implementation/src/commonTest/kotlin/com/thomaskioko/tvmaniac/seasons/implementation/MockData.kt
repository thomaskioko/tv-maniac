package com.thomaskioko.tvmaniac.seasons.implementation

import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.tmdb.api.model.GenreResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.SeasonsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.ShowDetailResponse

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
