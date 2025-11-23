package com.thomaskioko.tvmaniac.profile.presenter

public sealed interface ProfileAction {
    public data object LoginClicked : ProfileAction

    public data object SettingsClicked : ProfileAction

    public data object RefreshProfile : ProfileAction

    public data class MessageShown(val id: Long) : ProfileAction
}
