package com.thomaskioko.tvmaniac.following

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.presentation.following.ErrorLoadingShows
import com.thomaskioko.tvmaniac.presentation.following.FollowingContent
import com.thomaskioko.tvmaniac.presentation.following.FollowingShow
import com.thomaskioko.tvmaniac.presentation.following.FollowingState

val list = List(6) {
    FollowingShow(
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
                ErrorLoadingShows(message = "Something went Wrong"),
            )
        }
}
