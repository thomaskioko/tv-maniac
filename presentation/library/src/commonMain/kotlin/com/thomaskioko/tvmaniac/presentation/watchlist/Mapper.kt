package com.thomaskioko.tvmaniac.presentation.watchlist

import com.thomaskioko.tvmaniac.core.db.WatchedShow
import com.thomaskioko.tvmaniac.presentation.watchlist.model.LibraryItem
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

fun List<WatchedShow>?.entityToLibraryShowList(): PersistentList<LibraryItem> {
    return this?.map {
        LibraryItem(
            traktId = it.show_id.id,
            tmdbId = it.tmdb_id,
            title = it.title,
            posterImageUrl = it.poster_url,
        )
    }?.toPersistentList() ?: persistentListOf()
}
