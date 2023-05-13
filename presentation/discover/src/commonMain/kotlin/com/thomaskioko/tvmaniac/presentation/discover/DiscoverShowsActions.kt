package com.thomaskioko.tvmaniac.presentation.discover

sealed interface ShowsAction

object RetryLoading : ShowsAction
object ReloadFeatured : ShowsAction
object ReloadPopular : ShowsAction
object ReloadAnticipated : ShowsAction
object ReloadTrending : ShowsAction

data class ReloadCategory(val categoryId: Int) : ShowsAction
