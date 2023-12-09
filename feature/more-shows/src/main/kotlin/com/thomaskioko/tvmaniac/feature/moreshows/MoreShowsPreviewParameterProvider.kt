package com.thomaskioko.tvmaniac.feature.moreshows

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.presentation.moreshows.MoreShowsState
import com.thomaskioko.tvmaniac.presentation.moreshows.TvShow
import kotlinx.collections.immutable.toPersistentList

private val showList = List(6) {
    TvShow(
        traktId = 84958,
        title = "Loki",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    )
}.toPersistentList()

class MoreShowsPreviewParameterProvider : PreviewParameterProvider<MoreShowsState> {
    override val values: Sequence<MoreShowsState>
        get() {
            return sequenceOf(
                MoreShowsState(list = showList),
                MoreShowsState(
                    isLoading = true,
                    list = showList,
                    errorMessage = "Opps! Something went wrong",
                ),
            )
        }
}
