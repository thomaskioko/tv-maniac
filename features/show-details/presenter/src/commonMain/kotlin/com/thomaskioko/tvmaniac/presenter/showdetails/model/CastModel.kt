package com.thomaskioko.tvmaniac.presenter.showdetails.model

public data class CastModel(
    val id: Long,
    val name: String,
    val profileUrl: String? = null,
    val characterName: String,
)
