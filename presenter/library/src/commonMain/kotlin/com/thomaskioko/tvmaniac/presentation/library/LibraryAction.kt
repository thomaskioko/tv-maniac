package com.thomaskioko.tvmaniac.presentation.library

import com.thomaskioko.tvmaniac.presentation.library.model.LibrarySortOption

public sealed interface LibraryAction

public data class LibraryShowClicked(val traktId: Long) : LibraryAction

public data class LibraryQueryChanged(val query: String) : LibraryAction

public data object ClearLibraryQuery : LibraryAction

public data object ToggleSearchActive : LibraryAction

public data class ChangeListStyleClicked(val isGridMode: Boolean) : LibraryAction

public data class ChangeSortOption(val sortOption: LibrarySortOption) : LibraryAction

public data object ToggleFollowedOnly : LibraryAction

public data class MessageShown(val id: Long) : LibraryAction

public data object RefreshLibrary : LibraryAction
