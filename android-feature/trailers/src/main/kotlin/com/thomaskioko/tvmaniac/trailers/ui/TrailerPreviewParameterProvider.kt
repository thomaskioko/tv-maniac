package com.thomaskioko.tvmaniac.trailers.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.presenter.trailers.TrailerError
import com.thomaskioko.tvmaniac.presenter.trailers.TrailersContent
import com.thomaskioko.tvmaniac.presenter.trailers.TrailersState
import com.thomaskioko.tvmaniac.presenter.trailers.model.Trailer
import kotlinx.collections.immutable.toPersistentList

private val trailersList = List(4) {
    Trailer(
        showId = 1232,
        key = "er",
        name = "Trailer Name",
        youtubeThumbnailUrl = "",
    )
}
    .toPersistentList()

internal class TrailerPreviewParameterProvider : PreviewParameterProvider<TrailersState> {
    override val values: Sequence<TrailersState>
        get() {
            return sequenceOf(
                TrailersContent(trailersList = trailersList),
                TrailerError(errorMessage = "Opps! Something went wrong"),
            )
        }
}
