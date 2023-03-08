package com.thomaskioko.tvmaniac.profile

sealed interface ProfileActions

object ShowTraktDialog : ProfileActions
object DismissTraktDialog : ProfileActions
object TraktLogout : ProfileActions
object TraktLogin : ProfileActions
object RefreshTraktAuthToken : ProfileActions
object FetchTraktUserProfile : ProfileActions
object FetchUserStatsProfile : ProfileActions
