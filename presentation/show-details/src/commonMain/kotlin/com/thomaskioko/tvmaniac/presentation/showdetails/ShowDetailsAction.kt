package com.thomaskioko.tvmaniac.presentation.showdetails

sealed interface ShowDetailsAction

data object WebViewError : ShowDetailsAction
data object DismissWebViewError : ShowDetailsAction
data object BackClicked : ShowDetailsAction

data class SeasonClicked(val id: Long, val title: String) : ShowDetailsAction
data class ShowClicked(val id: Long) : ShowDetailsAction
data class WatchTrailerClicked(val id: Long) : ShowDetailsAction
data class ReloadShowDetails(val traktId: Long) : ShowDetailsAction

data class FollowShowClicked(
    val addToLibrary: Boolean,
) : ShowDetailsAction
