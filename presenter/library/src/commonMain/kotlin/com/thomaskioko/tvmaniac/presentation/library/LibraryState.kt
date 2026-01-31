package com.thomaskioko.tvmaniac.presentation.library

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.presentation.library.model.LibraryShowItem
import com.thomaskioko.tvmaniac.presentation.library.model.LibrarySortOption
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class LibraryState(
    val query: String = "",
    val isSearchActive: Boolean = false,
    val isGridMode: Boolean = true,
    val isRefreshing: Boolean = true,
    val sortOption: LibrarySortOption = LibrarySortOption.LAST_WATCHED,
    val followedOnly: Boolean = false,
    val items: ImmutableList<LibraryShowItem> = persistentListOf(),
    val message: UiMessage? = null,
) {
    val isEmpty: Boolean
        get() = items.isEmpty()

    val showLoading: Boolean
        get() = isRefreshing && isEmpty
}
