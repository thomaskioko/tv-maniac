package com.thomaskioko.tvmaniac.domain.statistics

import com.thomaskioko.tvmaniac.db.WatchStatus
import com.thomaskioko.tvmaniac.domain.statistics.model.AverageRating
import com.thomaskioko.tvmaniac.domain.statistics.model.DailyWatchCount
import com.thomaskioko.tvmaniac.domain.statistics.model.MonthlyWatchCount
import com.thomaskioko.tvmaniac.domain.statistics.model.PeakYear
import com.thomaskioko.tvmaniac.domain.statistics.model.PeriodSummary
import com.thomaskioko.tvmaniac.domain.statistics.model.RatingCount
import com.thomaskioko.tvmaniac.domain.statistics.model.ShowsTracked
import com.thomaskioko.tvmaniac.domain.statistics.model.TopWeekday
import com.thomaskioko.tvmaniac.domain.statistics.model.WatchDaysThisMonth
import com.thomaskioko.tvmaniac.domain.statistics.model.WatchDaysThisYear
import com.thomaskioko.tvmaniac.domain.statistics.model.WatchDuration
import com.thomaskioko.tvmaniac.domain.statistics.model.WatchStatistics
import com.thomaskioko.tvmaniac.domain.statistics.model.WatchStatusCount
import com.thomaskioko.tvmaniac.domain.statistics.model.WatchStreak
import com.thomaskioko.tvmaniac.domain.statistics.model.WeekdayWatchCount
import com.thomaskioko.tvmaniac.domain.statistics.model.YearlyWatchCount
import com.thomaskioko.tvmaniac.episodes.api.model.MostWatchedShow
import com.thomaskioko.tvmaniac.episodes.api.model.WatchedEpisodeRuntime
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.Inject
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

@Inject
public class WatchStatisticsCalculator(
    private val dateTimeProvider: DateTimeProvider,
) {

    public fun calculate(
        watches: List<WatchedEpisodeRuntime>,
        mostWatchedShows: List<MostWatchedShow>,
        showCountsByStatus: Map<WatchStatus, Long>,
        ratingCounts: Map<Int, Long>,
    ): WatchStatistics {
        val timeZone = dateTimeProvider.getTimeZone()
        val today = dateTimeProvider.now().toLocalDateTime(timeZone).date
        val watchedDays = watches.map { watch ->
            WatchedDay(
                date = Instant.fromEpochMilliseconds(watch.watchedAt).toLocalDateTime(timeZone).date,
                minutes = watch.runtimeMinutes ?: 0L,
            )
        }

        return WatchStatistics(
            totalWatchTime = WatchDuration(watchedDays.sumOf { it.minutes }),
            episodesWatched = watches.size.toLong(),
            showsTracked = calculateShowsTracked(showCountsByStatus),
            averageRating = calculateAverageRating(ratingCounts),
            topWeekday = calculateTopWeekday(watchedDays),
            streak = calculateStreak(watchedDays, today),
            watchDaysThisYear = calculateWatchDaysThisYear(watchedDays, today),
            watchDaysThisMonth = calculateWatchDaysThisMonth(watchedDays, today),
            lastThirtyDays = calculatePeriodSummary(
                watchedDays = watchedDays,
                from = today.plus(-LAST_PERIOD_DAYS, DateTimeUnit.DAY),
            ),
            peakYear = calculatePeakYear(watchedDays),
            dailyCounts = calculateDailyCounts(watchedDays, today),
            yearlyCounts = calculateYearlyCounts(watchedDays),
            monthlyCounts = calculateMonthlyCounts(watchedDays),
            weekdayCounts = calculateWeekdayCounts(watchedDays),
            mostWatchedShows = mostWatchedShows,
            showsByWatchStatus = getShowsByWatchStatus(showCountsByStatus),
            ratingDistribution = getRatingDistribution(ratingCounts),
        )
    }

    private fun calculateShowsTracked(showCountsByStatus: Map<WatchStatus, Long>): ShowsTracked =
        ShowsTracked(
            total = showCountsByStatus.values.sum(),
            completed = showCountsByStatus[WatchStatus.COMPLETED] ?: 0L,
        )

    private fun getShowsByWatchStatus(showCountsByStatus: Map<WatchStatus, Long>): List<WatchStatusCount> =
        WatchStatus.entries.map { status ->
            WatchStatusCount(status = status, showCount = showCountsByStatus[status] ?: 0L)
        }

    private fun getRatingDistribution(ratingCounts: Map<Int, Long>): List<RatingCount> =
        (LOWEST_RATING..HIGHEST_RATING).map { rating ->
            RatingCount(rating = rating, count = ratingCounts[rating] ?: 0L)
        }

    private fun calculateDailyCounts(watchedDays: List<WatchedDay>, today: LocalDate): List<DailyWatchCount> {
        val byDate = watchedDays.groupBy { it.date }
        val firstDay = LocalDate(today.year, 1, 1)
        return generateSequence(firstDay) { day ->
            if (day == today) null else day.plus(1, DateTimeUnit.DAY)
        }.map { day ->
            val onDay = byDate[day].orEmpty()
            DailyWatchCount(
                date = day,
                episodeCount = onDay.size,
                minutes = onDay.sumOf { it.minutes },
            )
        }.toList()
    }

    private fun calculateAverageRating(ratingCounts: Map<Int, Long>): AverageRating? {
        val total = ratingCounts.values.sum()
        if (total == 0L) return null
        val weighted = ratingCounts.entries.sumOf { (rating, count) -> rating * count }
        return AverageRating(average = weighted.toDouble() / total, ratedCount = total)
    }

    private fun calculateTopWeekday(watchedDays: List<WatchedDay>): TopWeekday? {
        val top = watchedDays.groupBy { it.date.dayOfWeek }
            .entries
            .maxByOrNull { it.value.size }
            ?: return null
        return TopWeekday(
            dayOfWeek = top.key,
            episodeCount = top.value.size,
            mostRecentDate = top.value.maxOf { it.date },
        )
    }

    private fun calculateWatchDaysThisYear(watchedDays: List<WatchedDay>, today: LocalDate): WatchDaysThisYear =
        WatchDaysThisYear(
            daysWatched = watchedDays.filter { it.date.year == today.year }.distinctBy { it.date }.size,
            daysElapsed = today.dayOfYear,
        )

    private fun calculateWatchDaysThisMonth(watchedDays: List<WatchedDay>, today: LocalDate): WatchDaysThisMonth =
        WatchDaysThisMonth(
            daysWatched = watchedDays
                .filter { it.date.year == today.year && it.date.month == today.month }
                .distinctBy { it.date }
                .size,
            daysElapsed = today.dayOfMonth,
        )

    private fun calculateYearlyCounts(watchedDays: List<WatchedDay>): List<YearlyWatchCount> =
        watchedDays.groupBy { it.date.year }
            .map { (year, days) ->
                YearlyWatchCount(year = year, episodeCount = days.size, minutes = days.sumOf { it.minutes })
            }
            .sortedBy { it.year }

    private fun calculateMonthlyCounts(watchedDays: List<WatchedDay>): List<MonthlyWatchCount> {
        val byMonth = watchedDays.groupBy { it.date.month }
        return Month.entries.map { month ->
            val inMonth = byMonth[month].orEmpty()
            MonthlyWatchCount(
                month = month,
                episodeCount = inMonth.size,
                minutes = inMonth.sumOf { it.minutes },
            )
        }
    }

    private fun calculateWeekdayCounts(watchedDays: List<WatchedDay>): List<WeekdayWatchCount> {
        val byWeekday = watchedDays.groupBy { it.date.dayOfWeek }
        return DayOfWeek.entries.map { dayOfWeek ->
            val onDay = byWeekday[dayOfWeek].orEmpty()
            WeekdayWatchCount(
                dayOfWeek = dayOfWeek,
                episodeCount = onDay.size,
                minutes = onDay.sumOf { it.minutes },
            )
        }
    }

    private fun calculatePeriodSummary(watchedDays: List<WatchedDay>, from: LocalDate): PeriodSummary {
        val inPeriod = watchedDays.filter { it.date >= from }
        return PeriodSummary(
            episodeCount = inPeriod.size,
            minutes = inPeriod.sumOf { it.minutes },
            activeDays = inPeriod.distinctBy { it.date }.size,
        )
    }

    private fun calculatePeakYear(watchedDays: List<WatchedDay>): PeakYear? {
        val top = watchedDays.groupBy { it.date.year }
            .entries
            .maxByOrNull { it.value.size }
            ?: return null
        return PeakYear(
            year = top.key,
            episodeCount = top.value.size,
            minutes = top.value.sumOf { it.minutes },
        )
    }

    private fun calculateStreak(watchedDays: List<WatchedDay>, today: LocalDate): WatchStreak {
        val days = watchedDays.map { it.date }.distinct().sorted()
        if (days.isEmpty()) return WatchStreak.NONE

        var longest = 1
        var longestEndIndex = 0
        var run = 1
        for (index in 1 until days.size) {
            run = if (days[index - 1].plus(1, DateTimeUnit.DAY) == days[index]) run + 1 else 1
            if (run > longest) {
                longest = run
                longestEndIndex = index
            }
        }

        val mostRecent = days.last()
        val stillRunning = mostRecent == today || mostRecent == today.plus(-1, DateTimeUnit.DAY)
        var current = 0
        if (stillRunning) {
            current = 1
            var index = days.size - 1
            while (index > 0 && days[index - 1].plus(1, DateTimeUnit.DAY) == days[index]) {
                current++
                index--
            }
        }

        return WatchStreak(
            currentDays = current,
            longestDays = longest,
            longestStart = days[longestEndIndex - longest + 1],
            longestEnd = days[longestEndIndex],
        )
    }

    private data class WatchedDay(val date: LocalDate, val minutes: Long)

    private companion object {
        const val LAST_PERIOD_DAYS = 30
        const val LOWEST_RATING = 1
        const val HIGHEST_RATING = 10
    }
}
