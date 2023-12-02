package com.thomaskioko.tvmaniac.presentation.profile

sealed interface ProfileActions

data object ShowTraktDialog : ProfileActions
data object DismissTraktDialog : ProfileActions
data object TraktLogoutClicked : ProfileActions
data object TraktLoginClicked : ProfileActions
data object SettingsClicked : ProfileActions
