package com.thomaskioko.tvmaniac.presentation.upnext

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.domain.continuewatching.model.UpNextSortOption
import com.thomaskioko.tvmaniac.presentation.upnext.model.UpNextEpisodeUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

public data class UpNextState(
    val isLoading: Boolean = true,
    val isSyncing: Boolean = false,
    val isRefreshing: Boolean = false,
    val sortOption: UpNextSortOption = UpNextSortOption.LAST_WATCHED,
    val episodes: ImmutableList<UpNextEpisodeUiModel> = persistentListOf(),
    val updatingEpisodeIds: ImmutableSet<Long> = persistentSetOf(),
    val isUpdating: Boolean = false,
    val message: UiMessage? = null,
) {
    val isEmpty: Boolean
        get() = episodes.isEmpty()

    val showLoading: Boolean
        get() = (isLoading || isSyncing) && isEmpty
}
