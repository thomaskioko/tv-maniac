package com.thomaskioko.tvmaniac.show_grid

sealed interface GridActions

data class ReloadShows(val category: Int)  : GridActions
data class LoadShows(val category: Int) : GridActions
