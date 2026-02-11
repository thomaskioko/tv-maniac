package com.thomaskioko.tvmaniac.presentation.upnext

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.domain.upnext.model.UpNextSortOption
import com.thomaskioko.tvmaniac.presentation.upnext.model.UpNextEpisodeUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class UpNextState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val sortOption: UpNextSortOption = UpNextSortOption.LAST_WATCHED,
    val episodes: ImmutableList<UpNextEpisodeUiModel> = persistentListOf(),
    val message: UiMessage? = null,
) {
    val isEmpty: Boolean
        get() = episodes.isEmpty()

    val showLoading: Boolean
        get() = isLoading && isEmpty
}
