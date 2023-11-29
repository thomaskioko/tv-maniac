package com.thomaskioko.tvmaniac.presentation.showdetails

sealed interface ShowDetailsAction

data object WebViewError : ShowDetailsAction

data object DismissWebViewError : ShowDetailsAction

data class ReloadShowDetails(val traktId: Long) : ShowDetailsAction

data class FollowShowClicked(
    val addToLibrary: Boolean,
) : ShowDetailsAction
