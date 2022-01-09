package com.thomaskioko.tvmaniac

import com.thomaskioko.tvmaniac.datasource.cache.EpisodesBySeasonId
import com.thomaskioko.tvmaniac.datasource.cache.Season
import com.thomaskioko.tvmaniac.remote.api.model.EpisodesResponse
import com.thomaskioko.tvmaniac.remote.api.model.SeasonResponse
import com.thomaskioko.tvmaniac.remote.api.model.ShowResponse
import com.thomaskioko.tvmaniac.remote.api.model.TvShowsResponse
import com.thomaskioko.tvmaniac.seasons.api.model.EpisodeUiModel
import com.thomaskioko.tvmaniac.shared.core.util.Resource
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
}
