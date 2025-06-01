package com.thomaskioko.tvmaniac.presentation.seasondetails

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.Cast
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.EpisodeDetailsModel
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonImagesModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class SeasonDetailsModel(
    val isUpdating: Boolean = false,
    val showGalleryBottomSheet: Boolean = false,
    val showSeasonWatchStateDialog: Boolean = false,
    val expandEpisodeItems: Boolean = false,
    val watchProgress: Float = 0F,
    val isSeasonWatched: Boolean = false,
    val episodeCount: Long,
    val seasonImages: ImmutableList<SeasonImagesModel>,
    val seasonId: Long,
    val seasonName: String,
    val seasonOverview: String,
    val imageUrl: String?,
    val episodeDetailsList: ImmutableList<EpisodeDetailsModel>,
    val seasonCast: ImmutableList<Cast>,
    val message: UiMessage? = null,
) {
    companion object {
        val Empty = SeasonDetailsModel(
            isUpdating = false,
            showGalleryBottomSheet = false,
            seasonImages = persistentListOf(),
            showSeasonWatchStateDialog = false,
            expandEpisodeItems = false,
            episodeCount = 0,
            watchProgress = 0F,
            isSeasonWatched = false,
            seasonId = 0,
            seasonName = "",
            seasonOverview = "",
            imageUrl = "",
            episodeDetailsList = persistentListOf(),
            seasonCast = persistentListOf(),
        )
    }
}
