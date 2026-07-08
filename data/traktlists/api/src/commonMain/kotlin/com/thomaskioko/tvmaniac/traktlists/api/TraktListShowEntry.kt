package com.thomaskioko.tvmaniac.traktlists.api

public data class TraktListShowEntry(
    val listId: Long,
    val traktId: Long,
    val listedAt: String,
    val pendingAction: String,
)
