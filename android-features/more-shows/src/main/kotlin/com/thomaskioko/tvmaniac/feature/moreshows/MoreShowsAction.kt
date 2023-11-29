package com.thomaskioko.tvmaniac.feature.moreshows

sealed interface GridActions

data class ReloadShows(val category: Long) : GridActions
data class LoadShows(val category: Long) : GridActions
