package com.thomaskioko.tvmaniac.seasondetails.presenter.model

data class Cast(
    val id: Long,
    val name: String,
    val profileUrl: String? = null,
    val characterName: String,
)
