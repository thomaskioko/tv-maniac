package com.thomaskioko.tvmaniac.presentation.showdetails.model

data class Cast(
    val id: Long,
    val name: String,
    val profileUrl: String? = null,
    val characterName: String,
)