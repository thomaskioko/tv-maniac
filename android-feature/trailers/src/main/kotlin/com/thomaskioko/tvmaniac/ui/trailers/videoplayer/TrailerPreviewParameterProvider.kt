package com.thomaskioko.tvmaniac.ui.trailers.videoplayer

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.presentation.trailers.TrailerError
import com.thomaskioko.tvmaniac.presentation.trailers.TrailersContent
import com.thomaskioko.tvmaniac.presentation.trailers.TrailersState
import com.thomaskioko.tvmaniac.presentation.trailers.model.Trailer
import kotlinx.collections.immutable.toPersistentList

private val trailersList =
  List(4) {
    Trailer(
      showId = 1232,
      key = "er",
      name = "Trailer Name",
      youtubeThumbnailUrl = "",
    )
  }
    .toPersistentList()

class TrailerPreviewParameterProvider : PreviewParameterProvider<TrailersState> {
  override val values: Sequence<TrailersState>
    get() {
      return sequenceOf(
        TrailersContent(trailersList = trailersList),
        TrailerError(errorMessage = "Opps! Something went wrong"),
      )
    }
}
