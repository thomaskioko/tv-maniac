package com.thomaskioko.tvmaniac.presentation.seasondetails.model

data class Cast(
    val id: Long,
    val name: String,
    val profileUrl: String? = null,
    val characterName: String,
)
