package com.thomaskioko.tvmaniac.showsgrid

sealed interface GridActions

data class ReloadShows(val category: Long) : GridActions
data class LoadShows(val category: Long) : GridActions
