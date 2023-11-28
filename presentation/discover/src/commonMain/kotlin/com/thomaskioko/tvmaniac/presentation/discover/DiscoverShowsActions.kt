package com.thomaskioko.tvmaniac.presentation.discover

sealed interface ShowsAction

data object RetryLoading : ShowsAction
data object SnackBarDismissed : ShowsAction

data class ReloadCategory(val categoryId: Long) : ShowsAction
