package com.thomaskioko.tvmaniac.ui.moreshows

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.paging.PagingData
import com.thomaskioko.tvmaniac.presentation.moreshows.MoreShowsState
import com.thomaskioko.tvmaniac.presentation.moreshows.TvShow
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

private val showList =
  List(6) {
      TvShow(
        title = "Loki",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
      )
    }
    .toPersistentList()

class MoreShowsPreviewParameterProvider : PreviewParameterProvider<MoreShowsState> {
  override val values: Sequence<MoreShowsState>
    get() {
      return sequenceOf(
        MoreShowsState(pagingDataFlow = flowOf(PagingData.from(showList))),
        MoreShowsState(
          isLoading = true,
          pagingDataFlow = emptyFlow(),
          errorMessage = "Opps! Something went wrong",
        ),
      )
    }
}
