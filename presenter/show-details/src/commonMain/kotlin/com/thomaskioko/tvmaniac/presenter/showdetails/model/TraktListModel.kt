package com.thomaskioko.tvmaniac.presenter.showdetails.model

public data class TraktListModel(
    val id: Long,
    val slug: String,
    val name: String,
    val description: String?,
    val itemCount: Long,
    val isShowInList: Boolean,
)
