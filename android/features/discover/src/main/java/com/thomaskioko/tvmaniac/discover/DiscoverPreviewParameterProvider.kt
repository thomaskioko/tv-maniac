package com.thomaskioko.tvmaniac.discover

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.category.api.model.Category
import com.thomaskioko.tvmaniac.shared.domain.discover.LoadingError
import com.thomaskioko.tvmaniac.shared.domain.discover.ShowResult
import com.thomaskioko.tvmaniac.shared.domain.discover.ShowsLoaded
import com.thomaskioko.tvmaniac.shared.domain.discover.ShowsState
import com.thomaskioko.tvmaniac.shared.domain.discover.model.TvShow

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

val showResultSuccess = ShowResult(
    featuredCategoryState = ShowResult.CategorySuccess(
        category = Category.FEATURED,
        tvShows = List(5) { shows },
    ),
    trendingCategoryState = ShowResult.CategorySuccess(
        category = Category.TRENDING,
        tvShows = List(10) { shows },
    ),
    popularCategoryState = ShowResult.CategorySuccess(
        category = Category.POPULAR,
        tvShows = List(10) { shows },
    ),
    anticipatedCategoryState = ShowResult.CategorySuccess(
        category = Category.ANTICIPATED,
        tvShows = List(10) { shows },
    ),
)

class DiscoverPreviewParameterProvider : PreviewParameterProvider<ShowsState> {
    override val values: Sequence<ShowsState>
        get() {
            return sequenceOf(
                ShowsLoaded(result = showResultSuccess),
                LoadingError(errorMessage = "Opps! Something went wrong"),
            )
        }
}
