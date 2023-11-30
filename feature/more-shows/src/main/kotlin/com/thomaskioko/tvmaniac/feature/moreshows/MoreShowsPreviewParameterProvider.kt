package com.thomaskioko.tvmaniac.feature.moreshows

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.feature.moreshows.model.TvShow
import kotlinx.collections.immutable.toPersistentList

private val showList = List(6) {
    TvShow(
        traktId = 84958,
        title = "Loki",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    )
}.toPersistentList()

class MoreShowsPreviewParameterProvider : PreviewParameterProvider<GridState> {
    override val values: Sequence<GridState>
        get() {
            return sequenceOf(
                ShowsLoaded(list = showList),
                LoadingContentError(errorMessage = "Opps! Something went wrong"),
            )
        }
}
