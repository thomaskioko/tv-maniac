package com.thomaskioko.tvmaniac.statistics.presenter.model

public data class WatchStatusItem(
    val id: WatchStatusItemId,
    val label: String,
    val showCount: Long,
    val fraction: Float,
)
