package com.thomaskioko.tvmaniac.presentation.watchlist

import com.thomaskioko.tvmaniac.core.db.LibraryShows
import com.thomaskioko.tvmaniac.presentation.watchlist.model.LibraryItem
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

fun List<LibraryShows>?.entityToLibraryShowList(): PersistentList<LibraryItem> {
    return this?.map {
        LibraryItem(
            tmdbId = it.id.id,
            title = it.name,
            posterImageUrl = it.poster_path,
        )
    }?.toPersistentList() ?: persistentListOf()
}
