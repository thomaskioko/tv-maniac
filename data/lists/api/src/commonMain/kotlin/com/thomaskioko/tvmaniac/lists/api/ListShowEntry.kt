package com.thomaskioko.tvmaniac.lists.api

public data class ListShowEntry(
    val listId: Long,
    val tmdbId: Long,
    val listedAt: String,
    val pendingAction: String,
)
