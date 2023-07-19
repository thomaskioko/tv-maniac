package com.thomaskioko.tvmaniac.presentation.discover

sealed interface ShowsAction

object RetryLoading : ShowsAction
object SnackBarDismissed : ShowsAction

data class ReloadCategory(val categoryId: Int) : ShowsAction
