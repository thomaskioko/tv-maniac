package com.thomaskioko.tvmaniac.lists.api

public data class ListShowEntry(
    val listId: Long,
    val traktId: Long,
    val listedAt: String,
    val pendingAction: String,
)
