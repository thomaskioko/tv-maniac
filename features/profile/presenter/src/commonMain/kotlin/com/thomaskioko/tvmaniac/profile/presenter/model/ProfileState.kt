package com.thomaskioko.tvmaniac.profile.presenter.model

import com.thomaskioko.tvmaniac.core.view.UiMessage

public data class ProfileState(
    val isLoading: Boolean,
    val userProfile: ProfileInfo?,
    val errorMessage: UiMessage? = null,
    val authenticated: Boolean,
    val userLists: SectionState<ProfileListItem> = SectionState.Loading,
    val inProgress: SectionState<ProfileShowItem> = SectionState.Loading,
    val recentlyWatched: SectionState<ProfileRecentItem> = SectionState.Loading,
    val library: SectionState<ProfileShowItem> = SectionState.Loading,
    val watchlist: SectionState<ProfileShowItem> = SectionState.Loading,
    val favorites: SectionState<ProfileShowItem> = SectionState.Loading,
) {
    val showLoading: Boolean
        get() = userProfile == null && errorMessage == null && isLoading

    val listCount: Int
        get() = (userLists as? SectionState.Content)?.items?.size ?: 0

    public companion object {
        public val DEFAULT_STATE: ProfileState = ProfileState(
            isLoading = true,
            userProfile = null,
            errorMessage = null,
            authenticated = false,
        )
    }
}
