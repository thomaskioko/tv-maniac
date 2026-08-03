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

public sealed interface WatchChange {
    public data class WatchEpisode(val params: MarkEpisodeWatchedParams) : WatchChange
    public data class UnwatchEpisode(val showId: Long, val episodeId: Long) : WatchChange
    public data class WatchSeason(
        val showId: Long,
        val seasonNumber: Long,
        val markPreviousSeasons: Boolean = false,
    ) : WatchChange
    public data class UnwatchSeason(val showId: Long, val seasonNumber: Long) : WatchChange
}

public sealed interface SeasonDialogState {
    public data object Hidden : SeasonDialogState
    public data object Gallery : SeasonDialogState

    public sealed interface Confirmation : SeasonDialogState {
        public val primaryChange: WatchChange
        public val secondaryChange: WatchChange? get() = null
    }

    public data class WatchSeasonConfirmation(
        override val primaryChange: WatchChange.WatchSeason,
    ) : Confirmation

    public data class UnwatchSeasonConfirmation(
        override val primaryChange: WatchChange.UnwatchSeason,
    ) : Confirmation

    public data class MarkPreviousEpisodesConfirmation(
        override val primaryChange: WatchChange.WatchEpisode,
        override val secondaryChange: WatchChange.WatchEpisode,
    ) : Confirmation

    public data class UnwatchEpisodeConfirmation(
        override val primaryChange: WatchChange.UnwatchEpisode,
    ) : Confirmation

    public data class MarkPreviousSeasonsConfirmation(
        override val primaryChange: WatchChange.WatchSeason,
        override val secondaryChange: WatchChange.WatchSeason,
    ) : Confirmation
}

public data class SeasonDetailsModel(
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
    val userRating: Int? = null,
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

    public companion object {
        public val Empty: SeasonDetailsModel = SeasonDetailsModel(
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
