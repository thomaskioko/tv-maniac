package com.thomaskioko.tvmaniac.presenter.showdetails.header

import com.thomaskioko.tvmaniac.core.view.UiMessage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class ShowDetailsHeaderState(
    val tmdbId: Long = 0,
    val title: String = "",
    val overview: String = "",
    val language: String? = null,
    val posterImageUrl: String? = null,
    val backdropImageUrl: String? = null,
    val year: String = "",
    val status: String? = null,
    val votes: Long = 0,
    val rating: Double = 0.0,
    val communityRating: Double? = null,
    val communityVotes: Long? = null,
    val userRating: Int? = null,
    val isInLibrary: Boolean = false,
    val genres: ImmutableList<String> = persistentListOf(),
    val canAddToList: Boolean = false,
    val isInList: Boolean = false,
    val listActionLabel: String = "",
    val isRefreshing: Boolean = false,
    val message: UiMessage? = null,
)
