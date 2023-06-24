package com.thomaskioko.tvmaniac.presentation.showdetails

sealed interface ShowDetailsAction

object WebViewError : ShowDetailsAction

object DismissWebViewError : ShowDetailsAction

data class ReloadShowDetails(val traktId: Long) : ShowDetailsAction

data class LoadShowDetails(
    val traktId: Long,
) : ShowDetailsAction

data class FollowShowClicked(
    val addToFollowed: Boolean,
) : ShowDetailsAction
