package com.thomaskioko.tvmaniac.presenter.showdetails.model

data class CastModel(
    val id: Long,
    val name: String,
    val profileUrl: String? = null,
    val characterName: String,
)
