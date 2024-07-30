package com.thomaskioko.tvmaniac.presentation.seasondetails

import com.thomaskioko.tvmaniac.presentation.seasondetails.model.Cast
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.EpisodeDetailsModel
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonImagesModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed interface SeasonDetailState {
  val isUpdating: Boolean
  val showGalleryBottomSheet: Boolean
  val seasonImages: ImmutableList<SeasonImagesModel>
}

data object InitialSeasonsState : SeasonDetailState {
  override val isUpdating: Boolean = true
  override val showGalleryBottomSheet: Boolean = false
  override val seasonImages: ImmutableList<SeasonImagesModel> = persistentListOf()
}

data class SeasonDetailsErrorState(
  override val isUpdating: Boolean = false,
  override val showGalleryBottomSheet: Boolean = false,
  override val seasonImages: ImmutableList<SeasonImagesModel> = persistentListOf(),
  val errorMessage: String?,
) : SeasonDetailState

data class SeasonDetailsLoaded(
  override val isUpdating: Boolean = false,
  override val showGalleryBottomSheet: Boolean = false,
  override val seasonImages: ImmutableList<SeasonImagesModel>,
  val showSeasonWatchStateDialog: Boolean = false,
  val expandEpisodeItems: Boolean = false,
  val episodeCount: Long,
  val watchProgress: Float = 0F,
  val isSeasonWatched: Boolean = false,
  val seasonId: Long,
  val seasonName: String,
  val seasonOverview: String,
  val imageUrl: String?,
  val episodeDetailsList: ImmutableList<EpisodeDetailsModel>,
  val seasonCast: ImmutableList<Cast>,
) : SeasonDetailState
