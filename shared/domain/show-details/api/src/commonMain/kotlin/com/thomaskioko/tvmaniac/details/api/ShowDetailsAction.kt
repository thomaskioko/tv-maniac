package com.thomaskioko.tvmaniac.details.api


sealed interface ShowDetailsAction

object WebViewError : ShowDetailsAction

object DismissWebViewError : ShowDetailsAction

data class ReloadShow(val traktId: Int) : ShowDetailsAction

data class LoadShowDetails(
    val traktId: Int
) : ShowDetailsAction

data class FollowShow(
    val traktId: Int,
    val addToWatchList: Boolean,
) : ShowDetailsAction