package com.thomaskioko.showdetails

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsState
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Season
import com.thomaskioko.tvmaniac.presentation.showdetails.model.ShowDetails
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Trailer
import kotlinx.collections.immutable.persistentListOf

private val showDetailsLoaded = ShowDetailsState(
    showDetails = ShowDetails(
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
        rating = 8.1,
        genres = persistentListOf("Horror", "Action"),
        status = "Returning Series",
        year = "2024",
    ),
    seasonsList = persistentListOf(
        Season(
            seasonId = 114355,
            tvShowId = 84958,
            name = "Season 1",
            seasonNumber = 1,
        ),
    ),
    trailersList = persistentListOf(
        Trailer(
            showId = 1232,
            key = "",
            name = "",
            youtubeThumbnailUrl = "",
        ),
        Trailer(
            showId = 1232,
            key = "",
            name = "",
            youtubeThumbnailUrl = "",
        ),
    ),
    similarShows = persistentListOf(),
    recommendedShowList = persistentListOf(),
    errorMessage = null,
    providers = persistentListOf(),
    castsList = persistentListOf(),
    hasWebViewInstalled = false,
)

class DetailPreviewParameterProvider : PreviewParameterProvider<ShowDetailsState> {
    override val values: Sequence<ShowDetailsState>
        get() {
            return sequenceOf(
                showDetailsLoaded,
            )
        }
}
