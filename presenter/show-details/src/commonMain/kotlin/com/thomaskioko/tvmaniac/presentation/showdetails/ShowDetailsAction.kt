package com.thomaskioko.tvmaniac.presentation.showdetails

import com.thomaskioko.tvmaniac.presentation.showdetails.model.ShowSeasonDetailsParam

sealed interface ShowDetailsAction

data object DismissShowsListSheet : ShowDetailsAction

data object ShowShowsListSheet : ShowDetailsAction

data object CreateCustomList : ShowDetailsAction

data object DismissErrorSnackbar : ShowDetailsAction

data object DetailBackClicked : ShowDetailsAction

data object ReloadShowDetails : ShowDetailsAction

data class SeasonClicked(val params: ShowSeasonDetailsParam) : ShowDetailsAction

data class DetailShowClicked(val id: Long) : ShowDetailsAction

data class WatchTrailerClicked(val id: Long) : ShowDetailsAction

data class FollowShowClicked(val addToLibrary: Boolean) : ShowDetailsAction
