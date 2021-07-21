package com.thomaskioko.tvmaniac.datasource.enums

enum class TimeWindow(val window: String) {
    WEEK("week"),
    DAY("day");

    companion object {
        operator fun get(window: String): TimeWindow {
            return when (window) {
                WEEK.window -> WEEK
                DAY.window -> DAY
                else -> WEEK
            }
        }
    }
}
