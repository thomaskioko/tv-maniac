package com.thomaskioko.tvmaniac.seasondetails

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.presentation.seasondetails.LoadingError
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsLoaded
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsState
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.Episode
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonDetails
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

val episode = Episode(
    id = 2534997,
    episodeNumberTitle = "E01 • Glorious Purpose",
    overview = "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
    voteCount = 42,
    runtime = 21,
    seasonId = 4654,
    imageUrl = "",
    episodeNumber = "01",
    episodeTitle = "Glorious Purpose",
    seasonEpisodeNumber = "S01 | E01",
)

val seasonDetails = SeasonDetails(
    seasonId = 1,
    seasonName = "Specials",
    episodeCount = 8,
    watchProgress = 0.4f,
    episodes = List(8) {
        episode
    }.toPersistentList(),
)

class SeasonPreviewParameterProvider : PreviewParameterProvider<SeasonDetailsState> {
    override val values: Sequence<SeasonDetailsState>
        get() {
            return sequenceOf(
                SeasonDetailsLoaded(
                    showTitle = "Loki",
                    seasonDetailsList = persistentListOf(seasonDetails),
                ),
                LoadingError(message = "Something went Wrong "),
            )
        }
}
