package com.thomaskioko.tvmaniac.presentation.seasondetails

import com.thomaskioko.tvmaniac.presentation.seasondetails.model.Cast
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.EpisodeDetailsModel
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonImagesModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class SeasonDetailsContent(
  val errorMessage: String? = null,
  val isLoading: Boolean = false,
  val showSeasonWatchStateDialog: Boolean = false,
  val expandEpisodeItems: Boolean = false,
  val showGalleryBottomSheet: Boolean = false,
  val seasonId: Long,
  val seasonName: String,
  val seasonOverview: String,
  val imageUrl: String?,
  val episodeCount: Long,
  val watchProgress: Float,
  val isSeasonWatched: Boolean,
  val episodeDetailsList: ImmutableList<EpisodeDetailsModel>,
  val seasonImages: ImmutableList<SeasonImagesModel>,
  val seasonCast: ImmutableList<Cast>,
) {
  companion object {
    val DEFAULT_SEASON_STATE =
      SeasonDetailsContent(
        seasonId = 0,
        seasonName = "",
        seasonOverview = "",
        imageUrl = "",
        episodeCount = 0,
        watchProgress = 0f,
        isSeasonWatched = false,
        episodeDetailsList = persistentListOf(),
        seasonImages = persistentListOf(),
        seasonCast = persistentListOf(),
      )
  }
}
