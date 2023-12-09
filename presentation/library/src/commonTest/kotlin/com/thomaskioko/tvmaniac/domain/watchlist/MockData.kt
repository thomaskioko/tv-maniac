package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.presentation.watchlist.model.LibraryItem
import kotlinx.collections.immutable.persistentListOf

val libraryItems = persistentListOf(
    LibraryItem(
        traktId = 84958,
        tmdbId = 849583,
        title = "Loki",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    ),
)
