package com.thomaskioko.tvmaniac.profile

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.presentation.profile.ProfileState
import com.thomaskioko.tvmaniac.presentation.profile.ProfileStats
import com.thomaskioko.tvmaniac.presentation.profile.UserInfo

class ProfilePreviewParameterProvider : PreviewParameterProvider<ProfileState> {
    override val values: Sequence<ProfileState>
        get() {
            return sequenceOf(
                ProfileState(),
                ProfileState(
                    isLoading = true,
                    showLogoutDialog = false,
                    loggedIn = false,
                    userInfo = null,
                    profileStats = null,
                ),
                ProfileState(
                    isLoading = false,
                    showLogoutDialog = false,
                    loggedIn = true,
                    userInfo = UserInfo(
                        slug = "me",
                        userName = "@j_Doe",
                        fullName = "J Doe",
                        userPicUrl = "image.png",
                    ),
                    profileStats = ProfileStats(
                        collectedShows = "2000",
                        showMonths = "08",
                        showDays = "120",
                        showHours = "120",
                        episodesWatched = "8.1k",
                    ),
                ),
            )
        }
}
