package com.thomaskioko.tvmaniac.presentation.showdetails

import com.thomaskioko.tvmaniac.presentation.showdetails.model.Casts
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Providers
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Season
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Show
import com.thomaskioko.tvmaniac.presentation.showdetails.model.ShowDetails
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Trailer
import kotlinx.collections.immutable.ImmutableList

data class ShowDetailsContent(
  val errorMessage: String? = null,
  val isUpdating: Boolean = false,
  val showDetails: ShowDetails? = null,
  val showInfo: ShowInfoState = ShowInfoState.Loading,
)

sealed interface ShowInfoState {
  data object Loading : ShowInfoState

  data object Empty : ShowInfoState

  data object Error : ShowInfoState

  data class Loaded(
    val hasWebViewInstalled: Boolean,
    val providers: ImmutableList<Providers>,
    val castsList: ImmutableList<Casts>,
    val seasonsList: ImmutableList<Season>,
    val recommendedShowList: ImmutableList<Show>,
    val similarShows: ImmutableList<Show>,
    val trailersList: ImmutableList<Trailer>,
    val openTrailersInYoutube: Boolean = false,
    val selectedSeasonIndex: Int = 0,
  ) : ShowInfoState
}
