package com.thomaskioko.tvmaniac.continuewatching.presenter.model

public data class StartWatchingItem(
    val traktId: Long,
    val title: String,
    val posterImageUrl: String?,
    val year: String?,
)
