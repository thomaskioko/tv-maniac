package com.thomaskioko.tvmaniac.presentation.library.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class LibraryShowItem(
    val traktId: Long,
    val tmdbId: Long?,
    val title: String,
    val posterImageUrl: String? = null,
    val status: String? = null,
    val year: String? = null,
    val rating: Double? = null,
    val genres: List<String>? = null,
    val seasonCount: Long = 0,
    val episodeCount: Long = 0,
    val isFollowed: Boolean = false,
    val watchProviders: ImmutableList<WatchProviderUiModel> = persistentListOf(),
)

public data class WatchProviderUiModel(
    val id: Long,
    val name: String?,
    val logoUrl: String?,
)
