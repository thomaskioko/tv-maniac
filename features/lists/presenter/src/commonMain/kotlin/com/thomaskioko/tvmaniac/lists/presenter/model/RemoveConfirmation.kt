package com.thomaskioko.tvmaniac.lists.presenter.model

public data class RemoveConfirmation(
    val tmdbId: Long,
    val title: String,
    val message: String,
    val confirmLabel: String,
)
