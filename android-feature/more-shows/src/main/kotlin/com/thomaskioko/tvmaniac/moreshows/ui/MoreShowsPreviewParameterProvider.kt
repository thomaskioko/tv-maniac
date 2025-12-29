package com.thomaskioko.tvmaniac.moreshows.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsState
import com.thomaskioko.tvmaniac.moreshows.presentation.TvShow
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.flowOf

public val showList: PersistentList<TvShow> = List(6) {
    TvShow(
        title = "Loki",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    )
}
    .toPersistentList()

internal class MoreShowsPreviewParameterProvider : PreviewParameterProvider<MoreShowsState> {
    override val values: Sequence<MoreShowsState>
        get() {
            return sequenceOf(
                MoreShowsState(
                    categoryTitle = "Upcoming",
                    pagingDataFlow = flowOf(PagingData.from(showList)),
                ),
                MoreShowsState(
                    categoryTitle = "Upcoming",
                    errorMessage = "Opps! Something went wrong",
                ),
            )
        }
}
