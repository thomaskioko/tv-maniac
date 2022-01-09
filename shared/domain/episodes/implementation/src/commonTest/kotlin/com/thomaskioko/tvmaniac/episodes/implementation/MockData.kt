package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.datasource.cache.EpisodesBySeasonId
import com.thomaskioko.tvmaniac.episodes.api.EpisodeUiModel
import com.thomaskioko.tvmaniac.remote.api.model.EpisodesResponse
import com.thomaskioko.tvmaniac.remote.api.model.SeasonResponse
import com.thomaskioko.tvmaniac.shared.core.util.Resource
import kotlinx.coroutines.flow.flowOf

object MockData {

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
