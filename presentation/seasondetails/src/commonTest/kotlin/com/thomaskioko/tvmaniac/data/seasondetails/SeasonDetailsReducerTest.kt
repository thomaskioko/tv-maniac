package com.thomaskioko.tvmaniac.data.seasondetails

import com.thomaskioko.tvmaniac.presentation.seasondetails.DismissSeasonDialog
import com.thomaskioko.tvmaniac.presentation.seasondetails.InitialSeasonsState
import com.thomaskioko.tvmaniac.presentation.seasondetails.OnEpisodeHeaderClicked
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsErrorState
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsLoaded
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonGalleryClicked
import com.thomaskioko.tvmaniac.presentation.seasondetails.UpdateSeasonWatchedState
import com.thomaskioko.tvmaniac.presentation.seasondetails.reducer
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class SeasonDetailsReducerTest {

  @Test
  fun `should toggle showGalleryBottomSheet for SeasonGalleryClicked action`() {
    val initialState = buildSeasonDetailsLoaded()
    val newState = reducer(initialState, SeasonGalleryClicked) as SeasonDetailsLoaded

    newState.showGalleryBottomSheet shouldBe true

    val toggledState = reducer(newState, SeasonGalleryClicked) as SeasonDetailsLoaded
    toggledState.showGalleryBottomSheet shouldBe false
  }

  @Test
  fun `should set showSeasonWatchStateDialog to false for DismissSeasonDialog action`() {
    val initialState = buildSeasonDetailsLoaded()
    val newState = reducer(initialState, DismissSeasonDialog) as SeasonDetailsLoaded
    newState.showSeasonWatchStateDialog shouldBe false
  }

  @Test
  fun `should set showSeasonWatchStateDialog to false for UpdateSeasonWatchedState action`() {
    val initialState = buildSeasonDetailsLoaded()
    val newState = reducer(initialState, UpdateSeasonWatchedState) as SeasonDetailsLoaded

    newState.showSeasonWatchStateDialog shouldBe false
  }

  @Test
  fun `should toggle expandEpisodeItems for OnEpisodeHeaderClicked action`() {
    val initialState = buildSeasonDetailsLoaded()
    val newState = reducer(initialState, OnEpisodeHeaderClicked) as SeasonDetailsLoaded

    newState.expandEpisodeItems shouldBe true

    val toggledState = reducer(newState, OnEpisodeHeaderClicked) as SeasonDetailsLoaded
    toggledState.expandEpisodeItems shouldBe false
  }

  @Test
  fun `should not change state for non-SeasonDetailsLoaded states`() {
    val initialState = InitialSeasonsState
    val newState = reducer(initialState, SeasonGalleryClicked)

    initialState shouldBe newState

    val errorState = SeasonDetailsErrorState(errorMessage = "Error")

    errorState shouldBe reducer(errorState, SeasonGalleryClicked)
  }
}
