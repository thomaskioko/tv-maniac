package com.thomaskioko.tvmaniac.following

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.domain.following.ErrorLoadingShows
import com.thomaskioko.tvmaniac.domain.following.FollowedShow
import com.thomaskioko.tvmaniac.domain.following.FollowingContent
import com.thomaskioko.tvmaniac.domain.following.FollowingState

val list = List(6) {
    FollowedShow(
        traktId = 84958,
        tmdbId = 84958,
        title = "Loki",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    )
}


class FollowingPreviewParameterProvider : PreviewParameterProvider<FollowingState> {
    override val values: Sequence<FollowingState>
        get() {
            return sequenceOf(
                FollowingContent(list = list),
                ErrorLoadingShows(message = "Something went Wrong")
            )
        }
}