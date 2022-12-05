package com.thomaskioko.tvmaniac.shows.api

sealed interface ShowsAction

object RetryLoading : ShowsAction

data class ReloadCategory(val categoryId: Int) : ShowsAction
