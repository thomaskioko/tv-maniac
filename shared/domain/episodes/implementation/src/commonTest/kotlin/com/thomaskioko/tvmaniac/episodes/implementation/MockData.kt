package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.db.EpisodesBySeasonId
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.episodes.api.model.EpisodeUiModel
import com.thomaskioko.tvmaniac.tmdb.api.model.EpisodesResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.SeasonResponse
import kotlinx.coroutines.flow.flowOf

object MockData {

    fun getShowSeasonsResponse() = SeasonResponse(
        air_date = "2021-06-09",
        name = "Season 1",
        overview = "",
        id = 114355,
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
                        votes = 42,
                        vote_average = 6.429,
                        episode_number = "01",
                        season_number = 1,
                        trakt_id = 126280,
                        epiosode_count = 6,
                        id_ = null,
                        overview_ = null,
                        tmdb_id = 1232,
                        title = "Glorious Purpose",
                    ),
                    EpisodesBySeasonId(
                        id = 2927202,
                        season_id = 114355,
                        name = "The Variant",
                        overview = "Mobius puts Loki to work, but not everyone at TVA is thrilled about the God of Mischief's presence.",
                        image_url = "https://image.tmdb.org/t/p/original/gqpcfkdmSsm6xiX2EsLkwUvA8g8.jpg",
                        votes = 23,
                        vote_average = 7.6,
                        season_number = 1,
                        episode_number = "02",
                        trakt_id = 126280,
                        epiosode_count = 6,
                        id_ = null,
                        tmdb_id = 1232,
                        title = "The Variant",
                        overview_ = null,
                    )
                )
            )
        )
}
