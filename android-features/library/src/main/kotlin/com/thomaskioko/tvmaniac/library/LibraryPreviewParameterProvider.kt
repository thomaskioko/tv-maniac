package com.thomaskioko.tvmaniac.library

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.presentation.watchlist.ErrorLoadingShows
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryContent
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryState
import com.thomaskioko.tvmaniac.presentation.watchlist.model.LibraryItem
import kotlinx.collections.immutable.toPersistentList

val list = List(6) {
    LibraryItem(
        traktId = 84958,
        tmdbId = 84958,
        title = "Loki",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    )
}.toPersistentList()

class LibraryPreviewParameterProvider : PreviewParameterProvider<LibraryState> {
    override val values: Sequence<LibraryState>
        get() {
            return sequenceOf(
                LibraryContent(list = list),
                ErrorLoadingShows(message = "Something went Wrong"),
            )
        }
}
