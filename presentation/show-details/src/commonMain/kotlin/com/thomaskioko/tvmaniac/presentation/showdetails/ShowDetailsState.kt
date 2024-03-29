package com.thomaskioko.tvmaniac.presentation.showdetails

import com.thomaskioko.tvmaniac.presentation.showdetails.model.Casts
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Providers
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Season
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Show
import com.thomaskioko.tvmaniac.presentation.showdetails.model.ShowDetails
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Trailer
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ShowDetailsState(
  val showDetails: ShowDetails,
  val isLoading: Boolean = false,
  val errorMessage: String?,
  val providers: ImmutableList<Providers>,
  val castsList: ImmutableList<Casts>,
  val seasonsList: ImmutableList<Season>,
  val recommendedShowList: ImmutableList<Show>,
  val similarShows: ImmutableList<Show>,
  val trailersList: ImmutableList<Trailer>,
  val hasWebViewInstalled: Boolean,
  val showPlayerErrorMessage: Boolean = false,
  // TODO: Fetch from settings repository
  val openTrailersInYoutube: Boolean = false,
  val selectedSeasonIndex: Int = 0,
) {
  companion object {
    val EMPTY_DETAIL_STATE =
      ShowDetailsState(
        showDetails = ShowDetails.EMPTY_SHOW,
        errorMessage = null,
        providers = persistentListOf(),
        castsList = persistentListOf(),
        seasonsList = persistentListOf(),
        trailersList = persistentListOf(),
        recommendedShowList = persistentListOf(),
        similarShows = persistentListOf(),
        hasWebViewInstalled = false,
      )
  }
}
