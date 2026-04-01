package com.thomaskioko.tvmaniac.traktlists.api

public data class TraktList(
    val id: Long,
    val slug: String,
    val name: String,
    val description: String?,
    val itemCount: Long,
    val isShowInList: Boolean,
)
