package com.thomaskioko.tvmaniac.presentation.episodedetail

import com.thomaskioko.tvmaniac.core.view.UiMessage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class EpisodeDetailSheetState(
    val isLoading: Boolean = true,
    val episodeTitle: String = "",
    val showName: String = "",
    val seasonEpisodeNumber: String = "",
    val imageUrl: String? = null,
    val overview: String? = null,
    val rating: Double? = null,
    val voteCount: Long? = null,
    val isWatched: Boolean = false,
    val availableActions: ImmutableList<EpisodeSheetActionItem> = persistentListOf(),
    val message: UiMessage? = null,
)

public enum class EpisodeSheetActionItem {
    TOGGLE_WATCHED,
    OPEN_SHOW,
    OPEN_SEASON,
    UNFOLLOW,
}
