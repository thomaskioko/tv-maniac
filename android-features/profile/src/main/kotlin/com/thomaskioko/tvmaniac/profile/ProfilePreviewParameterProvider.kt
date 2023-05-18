package com.thomaskioko.tvmaniac.profile

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.presentation.profile.ProfileContent
import com.thomaskioko.tvmaniac.presentation.profile.ProfileError
import com.thomaskioko.tvmaniac.presentation.profile.ProfileState
import com.thomaskioko.tvmaniac.presentation.profile.ProfileStats
import com.thomaskioko.tvmaniac.presentation.profile.ProfileStatsError
import com.thomaskioko.tvmaniac.presentation.profile.TraktUser

class ProfilePreviewParameterProvider : PreviewParameterProvider<ProfileState> {
    override val values: Sequence<ProfileState>
        get() {
            return sequenceOf(
                ProfileContent(
                    isLoading = true,
                    showTraktDialog = false,
                    loggedIn = false,
                    traktUser = null,
                    profileStats = null,
                ),
                ProfileContent(
                    isLoading = false,
                    showTraktDialog = false,
                    loggedIn = true,
                    traktUser = TraktUser(
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
                ProfileError(error = "Something went Wrong "),
                ProfileStatsError(error = "Something went Wrong "),
            )
        }
}
