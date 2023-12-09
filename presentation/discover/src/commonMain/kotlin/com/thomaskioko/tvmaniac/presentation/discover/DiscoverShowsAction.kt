package com.thomaskioko.tvmaniac.presentation.discover

sealed interface DiscoverShowAction

data object RetryLoading : DiscoverShowAction
data object SnackBarDismissed : DiscoverShowAction

data class ReloadCategory(val categoryId: Long) : DiscoverShowAction
data class ShowClicked(val id: Long) : DiscoverShowAction
data class LoadCategoryShows(val id: Long) : DiscoverShowAction
