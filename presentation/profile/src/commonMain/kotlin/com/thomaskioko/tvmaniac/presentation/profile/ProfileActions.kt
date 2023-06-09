package com.thomaskioko.tvmaniac.presentation.profile

sealed interface ProfileActions

object ShowTraktDialog : ProfileActions
object DismissTraktDialog : ProfileActions
object TraktLogoutClicked : ProfileActions
object TraktLoginClicked : ProfileActions
