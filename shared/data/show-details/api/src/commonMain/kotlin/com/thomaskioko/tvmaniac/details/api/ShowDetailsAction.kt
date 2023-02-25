package com.thomaskioko.tvmaniac.details.api


sealed interface ShowDetailsAction

object WebViewError : ShowDetailsAction

object DismissWebViewError : ShowDetailsAction

data class ReloadShow(val traktId: Long) : ShowDetailsAction

data class LoadShowDetails(
    val traktId: Long
) : ShowDetailsAction

data class FollowShow(
    val traktId: Long,
    val addToWatchList: Boolean,
) : ShowDetailsAction