package com.thomaskioko.tvmaniac.seasondetails.presenter

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedParams
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.Cast
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.EpisodeDetailsModel
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.SeasonImagesModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

sealed interface WatchOperation {
    data class MarkEpisodeWatched(val params: MarkEpisodeWatchedParams) : WatchOperation
    data class MarkEpisodeUnwatched(val showId: Long, val episodeId: Long) : WatchOperation
    data class MarkSeasonWatched(
        val showId: Long,
        val seasonNumber: Long,
        val markPreviousSeasons: Boolean = false,
    ) : WatchOperation
    data class MarkSeasonUnwatched(val showId: Long, val seasonNumber: Long) : WatchOperation
}

sealed interface SeasonDialogState {
    data object Hidden : SeasonDialogState
    data object Gallery : SeasonDialogState

    sealed interface Confirmation : SeasonDialogState {
        val primaryOperation: WatchOperation
        val secondaryOperation: WatchOperation? get() = null
    }

    data class UnwatchSeasonConfirmation(
        override val primaryOperation: WatchOperation.MarkSeasonUnwatched,
    ) : Confirmation

    data class MarkPreviousEpisodesConfirmation(
        override val primaryOperation: WatchOperation.MarkEpisodeWatched,
        override val secondaryOperation: WatchOperation.MarkEpisodeWatched,
    ) : Confirmation

    data class UnwatchEpisodeConfirmation(
        override val primaryOperation: WatchOperation.MarkEpisodeUnwatched,
    ) : Confirmation

    data class MarkPreviousSeasonsConfirmation(
        override val primaryOperation: WatchOperation.MarkSeasonWatched,
        override val secondaryOperation: WatchOperation.MarkSeasonWatched,
    ) : Confirmation
}

data class SeasonDetailsModel(
    val isSeasonDetailsUpdating: Boolean = false,
    val isCheckingPreviousSeasons: Boolean = false,
    val dialogState: SeasonDialogState = SeasonDialogState.Hidden,
    val expandEpisodeItems: Boolean = false,
    val watchProgress: Float = 0F,
    val isSeasonWatched: Boolean = false,
    val watchedEpisodeCount: Int = 0,
    val hasUnwatchedInPreviousSeasons: Boolean = false,
    val isEpisodeUpdating: Boolean = false,
    val updatingEpisodeIds: ImmutableSet<Long> = persistentSetOf(),
    val isSeasonUpdatingProcessing: Boolean = false,
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
    val isRefreshing: Boolean
        get() = isSeasonDetailsUpdating || isCheckingPreviousSeasons || isEpisodeUpdating

    val hasContent: Boolean
        get() = episodeDetailsList.isNotEmpty() || seasonOverview.isNotEmpty()

    val showError: Boolean
        get() = message != null && !hasContent && !isRefreshing

    val isDialogVisible: Boolean
        get() = dialogState !is SeasonDialogState.Hidden

    val isGalleryVisible: Boolean
        get() = dialogState is SeasonDialogState.Gallery

    companion object {
        val Empty = SeasonDetailsModel(
            isSeasonDetailsUpdating = false,
            isCheckingPreviousSeasons = false,
            dialogState = SeasonDialogState.Hidden,
            expandEpisodeItems = false,
            episodeCount = 0,
            watchProgress = 0F,
            isSeasonWatched = false,
            watchedEpisodeCount = 0,
            hasUnwatchedInPreviousSeasons = false,
            updatingEpisodeIds = persistentSetOf(),
            isSeasonUpdatingProcessing = false,
            seasonId = 0,
            seasonName = "",
            seasonOverview = "",
            imageUrl = "",
            seasonImages = persistentListOf(),
            episodeDetailsList = persistentListOf(),
            seasonCast = persistentListOf(),
        )
    }
}
