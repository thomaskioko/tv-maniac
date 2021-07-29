package com.thomaskioko.tvmaniac.presentation.model

data class TrailerModel(
    val id: Long,
    val youtubeKey: String,
    val title: String,
    val trailerProvider: String,
    val trailerResolution: Long,
    val videoType: String,
)
