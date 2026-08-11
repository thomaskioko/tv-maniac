package com.thomaskioko.tvmaniac.domain.statistics.model

import kotlinx.datetime.LocalDate

public data class WatchStreak(
    val currentDays: Int,
    val longestDays: Int,
    val longestStart: LocalDate?,
    val longestEnd: LocalDate?,
) {
    public companion object {
        public val NONE: WatchStreak = WatchStreak(
            currentDays = 0,
            longestDays = 0,
            longestStart = null,
            longestEnd = null,
        )
    }
}
