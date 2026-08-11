package com.thomaskioko.tvmaniac.domain.statistics.model

import com.thomaskioko.tvmaniac.data.ratings.api.HighestRatedShow
import com.thomaskioko.tvmaniac.episodes.api.model.MostWatchedShow

public data class WatchStatistics(
    val totalWatchTime: WatchDuration,
    val episodesWatched: Long,
    val showsTracked: ShowsTracked,
    val averageRating: AverageRating?,
    val topWeekday: TopWeekday?,
    val streak: WatchStreak,
    val watchDaysThisYear: WatchDaysThisYear,
    val watchDaysThisMonth: WatchDaysThisMonth,
    val lastThirtyDays: PeriodSummary,
    val peakYear: PeakYear?,
    val dailyCounts: List<DailyWatchCount>,
    val yearlyCounts: List<YearlyWatchCount>,
    val monthlyCounts: List<MonthlyWatchCount>,
    val weekdayCounts: List<WeekdayWatchCount>,
    val mostWatchedShows: List<MostWatchedShow>,
    val highestRatedShows: List<HighestRatedShow>,
    val showsByWatchStatus: List<WatchStatusCount>,
    val ratingDistribution: List<RatingCount>,
    val genreBreakdown: List<GenreCount>,
    val releaseYears: List<ReleaseYearCount>,
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
            watchDaysThisMonth = WatchDaysThisMonth(daysWatched = 0, daysElapsed = 0),
            lastThirtyDays = PeriodSummary.NONE,
            peakYear = null,
            dailyCounts = emptyList(),
            yearlyCounts = emptyList(),
            monthlyCounts = emptyList(),
            weekdayCounts = emptyList(),
            mostWatchedShows = emptyList(),
            highestRatedShows = emptyList(),
            showsByWatchStatus = emptyList(),
            ratingDistribution = emptyList(),
            genreBreakdown = emptyList(),
            releaseYears = emptyList(),
        )
    }
}
