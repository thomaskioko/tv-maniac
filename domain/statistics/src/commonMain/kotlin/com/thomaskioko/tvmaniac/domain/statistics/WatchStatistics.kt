package com.thomaskioko.tvmaniac.domain.statistics

import com.thomaskioko.tvmaniac.db.WatchStatus
import com.thomaskioko.tvmaniac.episodes.api.model.MostWatchedShow
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

public data class WatchStatistics(
    val totalWatchTime: WatchDuration,
    val episodesWatched: Long,
    val showsTracked: ShowsTracked,
    val averageRating: AverageRating?,
    val topWeekday: TopWeekday?,
    val streak: WatchStreak,
    val watchDaysThisYear: WatchDaysThisYear,
    val lastThirtyDays: PeriodSummary,
    val peakYear: PeakYear?,
    val dailyCounts: List<DailyWatchCount>,
    val mostWatchedShows: List<MostWatchedShow>,
    val showsByWatchStatus: List<WatchStatusCount>,
    val ratingDistribution: List<RatingCount>,
) {
    val hasWatchHistory: Boolean
        get() = episodesWatched > 0

    val hasRuntimeData: Boolean
        get() = totalWatchTime.totalMinutes > 0

    public companion object {
        public val EMPTY: WatchStatistics = WatchStatistics(
            totalWatchTime = WatchDuration.NONE,
            episodesWatched = 0,
            showsTracked = ShowsTracked(total = 0, completed = 0),
            averageRating = null,
            topWeekday = null,
            streak = WatchStreak.NONE,
            watchDaysThisYear = WatchDaysThisYear(daysWatched = 0, daysElapsed = 0),
            lastThirtyDays = PeriodSummary.NONE,
            peakYear = null,
            dailyCounts = emptyList(),
            mostWatchedShows = emptyList(),
            showsByWatchStatus = emptyList(),
            ratingDistribution = emptyList(),
        )
    }
}

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

public data class ShowsTracked(
    val total: Long,
    val completed: Long,
) {
    val completionRate: Int
        get() = if (total == 0L) 0 else ((completed * 100) / total).toInt()
}

public data class AverageRating(
    val average: Double,
    val ratedCount: Long,
)

public data class TopWeekday(
    val dayOfWeek: DayOfWeek,
    val episodeCount: Int,
)

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

public data class WatchDaysThisYear(
    val daysWatched: Int,
    val daysElapsed: Int,
)

public data class PeriodSummary(
    val episodeCount: Int,
    val minutes: Long,
    val activeDays: Int,
) {
    public companion object {
        public val NONE: PeriodSummary = PeriodSummary(episodeCount = 0, minutes = 0, activeDays = 0)
    }
}

public data class PeakYear(
    val year: Int,
    val episodeCount: Int,
    val minutes: Long,
)

public data class DailyWatchCount(
    val date: LocalDate,
    val episodeCount: Int,
    val minutes: Long,
)

public data class WatchStatusCount(
    val status: WatchStatus,
    val showCount: Long,
)

public data class RatingCount(
    val rating: Int,
    val count: Long,
)
