package com.thomaskioko.tvmaniac.domain.statistics.model

public data class WatchDuration(val totalMinutes: Long) {
    val days: Long get() = totalMinutes / MINUTES_PER_DAY
    val hours: Long get() = (totalMinutes % MINUTES_PER_DAY) / MINUTES_PER_HOUR
    val minutes: Long get() = totalMinutes % MINUTES_PER_HOUR

    public companion object {
        private const val MINUTES_PER_HOUR = 60L
        private const val MINUTES_PER_DAY = 1440L
        public val NONE: WatchDuration = WatchDuration(0)
    }
}
