package com.thomaskioko.tvmaniac.discover

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.presentation.discover.DataLoaded
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverState
import com.thomaskioko.tvmaniac.presentation.discover.EmptyState
import com.thomaskioko.tvmaniac.presentation.discover.ErrorState
import com.thomaskioko.tvmaniac.presentation.discover.model.DiscoverShow
import kotlinx.collections.immutable.toImmutableList

val discoverShow = DiscoverShow(
    tmdbId = 84958,
    title = "Loki",
    posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
)

val discoverContentSuccess = DataLoaded(
    featuredShows = createDiscoverShowList(5),
    topRatedShows = createDiscoverShowList(),
    popularShows = createDiscoverShowList(),
    upcomingShows = createDiscoverShowList(),
)

private fun createDiscoverShowList(size: Int = 20) = List(size) { discoverShow }.toImmutableList()

class DiscoverPreviewParameterProvider : PreviewParameterProvider<DiscoverState> {
    override val values: Sequence<DiscoverState>
        get() {
            return sequenceOf(
                EmptyState,
                discoverContentSuccess,
                ErrorState(errorMessage = "Opps! Something went wrong"),
            )
        }
}
