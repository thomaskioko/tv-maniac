package com.thomaskioko.tvmaniac.presentation.library

import com.thomaskioko.tvmaniac.presentation.library.model.LibrarySortOption
import com.thomaskioko.tvmaniac.presentation.library.model.ShowStatus

public sealed interface LibraryAction

public data class LibraryShowClicked(val traktId: Long) : LibraryAction

public data class LibraryQueryChanged(val query: String) : LibraryAction

public data object ClearLibraryQuery : LibraryAction

public data object ToggleSearchActive : LibraryAction

public data class ChangeListStyleClicked(val isGridMode: Boolean) : LibraryAction

public data class ChangeSortOption(val sortOption: LibrarySortOption) : LibraryAction

public data object ToggleFollowedOnly : LibraryAction

public data class ToggleGenreFilter(val genre: String) : LibraryAction

public data class ToggleStatusFilter(val status: ShowStatus) : LibraryAction

public data object ClearFilters : LibraryAction

public data class MessageShown(val id: Long) : LibraryAction

public data object RefreshLibrary : LibraryAction
