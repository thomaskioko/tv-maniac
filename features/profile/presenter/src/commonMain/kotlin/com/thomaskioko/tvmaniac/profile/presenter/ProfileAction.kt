package com.thomaskioko.tvmaniac.profile.presenter

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource

public sealed interface ProfileAction {
    public data class LoginClicked(val provider: SyncProviderSource) : ProfileAction

    public data object SettingsClicked : ProfileAction

    public data object ViewListsClicked : ProfileAction

    public data object RefreshProfile : ProfileAction

    public data class ShowClicked(val showId: Long) : ProfileAction

    public data class MessageShown(val id: Long) : ProfileAction
}
