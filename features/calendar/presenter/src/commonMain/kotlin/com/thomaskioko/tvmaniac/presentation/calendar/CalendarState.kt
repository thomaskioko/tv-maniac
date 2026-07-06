package com.thomaskioko.tvmaniac.presentation.calendar

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.presentation.calendar.model.CalendarDateGroup
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class CalendarState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isLocked: Boolean = false,
    val lockedTitle: String = "",
    val lockedMessage: String = "",
    val lockedBadgeText: String = "",
    val lockedActionText: String = "",
    val lockedContentDescription: String = "",
    val weekOffset: Int = 0,
    val weekLabel: String = "",
    val canNavigatePrevious: Boolean = false,
    val canNavigateNext: Boolean = true,
    val emptyTitle: String = "",
    val emptyMessage: String = "",
    val loginTitle: String = "",
    val loginMessage: String = "",
    val moreEpisodesFormat: String = "",
    val dateGroups: ImmutableList<CalendarDateGroup> = persistentListOf(),
    val message: UiMessage? = null,
) {
    val isEmpty: Boolean
        get() = dateGroups.isEmpty()

    val showLoading: Boolean
        get() = isLoading && isEmpty
}
