package com.thomaskioko.tvmaniac.presentation.library

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.presentation.library.model.LibraryShowItem
import com.thomaskioko.tvmaniac.presentation.library.model.LibrarySortOption
import com.thomaskioko.tvmaniac.presentation.library.model.ShowStatus
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

public data class LibraryState(
    val query: String = "",
    val isSearchActive: Boolean = false,
    val isGridMode: Boolean = true,
    val isRefreshing: Boolean = true,
    val sortOption: LibrarySortOption = LibrarySortOption.ADDED_DESC,
    val followedOnly: Boolean = false,
    val availableGenres: ImmutableList<String> = persistentListOf(),
    val selectedGenres: ImmutableSet<String> = persistentSetOf(),
    val availableStatuses: ImmutableList<ShowStatus> = persistentListOf(),
    val selectedStatuses: ImmutableSet<ShowStatus> = persistentSetOf(),
    val items: ImmutableList<LibraryShowItem> = persistentListOf(),
    val message: UiMessage? = null,
) {
    val isEmpty: Boolean
        get() = items.isEmpty()

    val showLoading: Boolean
        get() = isRefreshing && isEmpty

    val hasActiveFilters: Boolean
        get() = selectedGenres.isNotEmpty() || selectedStatuses.isNotEmpty()
}
