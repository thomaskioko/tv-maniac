package com.thomaskioko.tvmaniac.discover

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.presentation.discover.DataLoaded
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverState
import com.thomaskioko.tvmaniac.presentation.discover.model.TvShow
import kotlinx.collections.immutable.toImmutableList

val shows = TvShow(
    traktId = 84958,
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
    genres = listOf("Horror", "Action"),
    status = "Returning Series",
    year = "2024",
)

val discoverContentSuccess = DataLoaded(
    recommendedShows = createShowList(5),
    trendingShows = createShowList(),
    popularShows = createShowList(),
    anticipatedShows = createShowList(),
)

private fun createShowList(size: Int = 20) = List(size) { shows }.toImmutableList()

class DiscoverPreviewParameterProvider : PreviewParameterProvider<DiscoverState> {
    override val values: Sequence<DiscoverState>
        get() {
            return sequenceOf(
                discoverContentSuccess,
                DataLoaded(errorMessage = "Opps! Something went wrong"),
            )
        }
}
