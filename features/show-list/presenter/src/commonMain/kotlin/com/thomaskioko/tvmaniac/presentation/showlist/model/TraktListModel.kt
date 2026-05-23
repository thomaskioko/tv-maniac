package com.thomaskioko.tvmaniac.presentation.showlist.model

public data class TraktListModel(
    val id: Long,
    val slug: String,
    val name: String,
    val description: String?,
    val showCountText: String,
    val isShowInList: Boolean,
    val isToggling: Boolean = false,
)
