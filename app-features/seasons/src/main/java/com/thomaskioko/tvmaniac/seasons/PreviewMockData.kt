package com.thomaskioko.tvmaniac.seasons

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.episodes.api.model.EpisodeUiModel
import com.thomaskioko.tvmaniac.seasons.api.model.SeasonUiModel

class EpisodeUiModelProvider : PreviewParameterProvider<EpisodeUiModel> {
    override val values: Sequence<EpisodeUiModel>
        get() = sequenceOf(
            EpisodeUiModel(
                id = 2534997,
                name = "Glorious Purpose",
                overview = "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
                voteCount = 42,
                voteAverage = 6.429,
                seasonNumber = 1,
                seasonId = 4654,
                imageUrl = "",
                episodeNumber = "01"
            )
        )
}

val seasons = listOf(
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
        episodeCount = 6
    )
)

val episodes = listOf(
    EpisodeUiModel(
        id = 2534997,
        name = "Glorious Purpose",
        overview = "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
        voteCount = 42,
        voteAverage = 6.429,
        seasonNumber = 1,
        seasonId = 4654,
        imageUrl = "",
        episodeNumber = "01"
    ),
    EpisodeUiModel(
        id = 2534997,
        name = "Glorious Purpose",
        overview = "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
        voteCount = 42,
        voteAverage = 6.429,
        seasonNumber = 1,
        seasonId = 46524,
        imageUrl = "",
        episodeNumber = "02"
    ),

)
