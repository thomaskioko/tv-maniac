package com.thomaskioko.tvmaniac.profile.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileInfo
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileState
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileStats

internal val unauthenticatedState = ProfileState(
    isLoading = false,
    userProfile = null,
    errorMessage = null,
    authenticated = false,
)

internal val authenticatedState = ProfileState(
    isLoading = false,
    userProfile = ProfileInfo(
        slug = "testuser",
        username = "testuser",
        fullName = "Test User",
        avatarUrl = null,
        stats = ProfileStats(
            showsWatched = 42,
            episodesWatched = 256,
            years = 0,
            months = 0,
            days = 5,
            hours = 12,
            minutes = 30,
        ),
        backgroundUrl = null,
    ),
    errorMessage = null,
    authenticated = true,
)

internal class ProfilePreviewParameterProvider : PreviewParameterProvider<ProfileState> {
    override val values: Sequence<ProfileState>
        get() {
            return sequenceOf(
                unauthenticatedState,
                authenticatedState,
            )
        }
}
