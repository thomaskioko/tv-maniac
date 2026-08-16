package com.thomaskioko.tvmaniac.watchdateselection.presenter

import kotlinx.datetime.LocalDate

public data class WatchDateSelectionState(
    val title: String = "",
    val justNowLabel: String = "",
    val releaseDateLabel: String = "",
    val otherDateLabel: String = "",
    val unknownDateLabel: String = "",
    val isReleaseDateEnabled: Boolean = false,
    val currentWatchedAtLabel: String? = null,
    val maxSelectableDate: LocalDate,
)
