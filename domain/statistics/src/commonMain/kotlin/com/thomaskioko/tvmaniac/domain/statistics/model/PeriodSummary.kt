package com.thomaskioko.tvmaniac.domain.statistics.model

public data class PeriodSummary(
    val episodeCount: Int,
    val minutes: Long,
    val activeDays: Int,
) {
    public companion object {
        public val NONE: PeriodSummary = PeriodSummary(episodeCount = 0, minutes = 0, activeDays = 0)
    }
}
