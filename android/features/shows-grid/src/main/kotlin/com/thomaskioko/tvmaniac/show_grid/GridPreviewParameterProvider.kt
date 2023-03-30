package com.thomaskioko.tvmaniac.show_grid

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.show_grid.model.TvShow


private val showList = List(6) {
    TvShow(
        traktId = 84958,
        title = "Loki",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    )
}

class GridPreviewParameterProvider : PreviewParameterProvider<GridState> {
    override val values: Sequence<GridState>
        get() {
            return sequenceOf(
                ShowsLoaded(list = showList),
                LoadingContentError(errorMessage = "Opps! Something went wrong")
            )
        }
}