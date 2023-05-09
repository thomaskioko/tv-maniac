package com.thomaskioko.tvmaniac.videoplayer

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.presentation.trailers.TrailerError
import com.thomaskioko.tvmaniac.presentation.trailers.TrailersLoaded
import com.thomaskioko.tvmaniac.presentation.trailers.TrailersState
import com.thomaskioko.tvmaniac.presentation.trailers.model.Trailer

private val trailersList = List(4) {
    Trailer(
        showId = 1232,
        key = "er",
        name = "Trailer Name",
        youtubeThumbnailUrl = "",
    )
}

class TrailerPreviewParameterProvider : PreviewParameterProvider<TrailersState> {
    override val values: Sequence<TrailersState>
        get() {
            return sequenceOf(
                TrailersLoaded(trailersList = trailersList),
                TrailerError(errorMessage = "Opps! Something went wrong"),
            )
        }
}
