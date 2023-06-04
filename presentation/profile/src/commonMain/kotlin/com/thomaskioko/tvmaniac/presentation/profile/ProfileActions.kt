package com.thomaskioko.tvmaniac.presentation.profile

sealed interface ProfileActions

object ShowTraktDialog : ProfileActions
object DismissTraktDialog : ProfileActions
object TraktLogout : ProfileActions
object TraktLogin : ProfileActions
object FetchTraktUserProfile : ProfileActions
object FetchUserStatsProfile : ProfileActions
