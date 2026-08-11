package com.thomaskioko.tvmaniac.statistics.presenter

import com.thomaskioko.tvmaniac.db.WatchStatus
import com.thomaskioko.tvmaniac.domain.statistics.model.AverageRating
import com.thomaskioko.tvmaniac.domain.statistics.model.DailyWatchCount
import com.thomaskioko.tvmaniac.domain.statistics.model.PeakYear
import com.thomaskioko.tvmaniac.domain.statistics.model.TopWeekday
import com.thomaskioko.tvmaniac.domain.statistics.model.WatchDaysThisYear
import com.thomaskioko.tvmaniac.domain.statistics.model.WatchStatistics
import com.thomaskioko.tvmaniac.domain.statistics.model.WatchStreak
import com.thomaskioko.tvmaniac.i18n.PluralsResourceKey
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.statistics.presenter.model.ActivityBar
import com.thomaskioko.tvmaniac.statistics.presenter.model.GenreSlice
import com.thomaskioko.tvmaniac.statistics.presenter.model.HeatMap
import com.thomaskioko.tvmaniac.statistics.presenter.model.RatingBar
import com.thomaskioko.tvmaniac.statistics.presenter.model.ShowRowItem
import com.thomaskioko.tvmaniac.statistics.presenter.model.StatisticTile
import com.thomaskioko.tvmaniac.statistics.presenter.model.StatisticTileId
import com.thomaskioko.tvmaniac.statistics.presenter.model.WatchHeatMap
import com.thomaskioko.tvmaniac.statistics.presenter.model.WatchStatusItem
import com.thomaskioko.tvmaniac.statistics.presenter.model.WatchStatusItemId
import com.thomaskioko.tvmaniac.statistics.presenter.model.WatchTime
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.isoDayNumber

@Inject
public class StatisticsStateMapper(
    private val localizer: Localizer,
    private val formatterUtil: FormatterUtil,
    private val dateTimeProvider: DateTimeProvider,
) {

    public fun toWatchTime(statistics: WatchStatistics): WatchTime? {
        if (!statistics.hasRuntimeData) return null
        val duration = statistics.totalWatchTime
        return WatchTime(days = duration.days, hours = duration.hours, minutes = duration.minutes)
    }

    public fun toTiles(statistics: WatchStatistics): ImmutableList<StatisticTile> = buildList {
        statistics.peakYear?.let { peak ->
            add(
                StatisticTile(
                    id = StatisticTileId.PeakYear,
                    label = localizer.getString(StringResourceKey.LabelStatisticsPeakYear),
                    value = peak.year.toString(),
                    caption = localizer.getPlural(PluralsResourceKey.EpisodeCount, peak.episodeCount),
                ),
            )
        }
        add(
            StatisticTile(
                id = StatisticTileId.WatchDaysThisYear,
                label = localizer.getString(StringResourceKey.LabelStatisticsWatchDays),
                value = "${statistics.watchDaysThisYear.daysWatched}/${statistics.watchDaysThisYear.daysElapsed}",
                caption = localizer.getString(StringResourceKey.LabelStatisticsWatchDaysCaption),
            ),
        )
        add(
            StatisticTile(
                id = StatisticTileId.WatchDaysThisMonth,
                label = localizer.getString(StringResourceKey.LabelStatisticsWatchDaysMonth),
                value = "${statistics.watchDaysThisMonth.daysWatched}/${statistics.watchDaysThisMonth.daysElapsed}",
                caption = localizer.getString(StringResourceKey.LabelStatisticsWatchDaysCaption),
            ),
        )
        statistics.topWeekday?.let { weekday ->
            add(
                StatisticTile(
                    id = StatisticTileId.TopWeekday,
                    label = localizer.getString(StringResourceKey.LabelStatisticsTopWeekday),
                    value = dateTimeProvider.formatDayOfWeek(weekday.mostRecentDate),
                    caption = localizer.getPlural(PluralsResourceKey.EpisodeCount, weekday.episodeCount),
                ),
            )
        }
        add(
            StatisticTile(
                id = StatisticTileId.LastThirtyDays,
                label = localizer.getString(StringResourceKey.LabelStatisticsLastThirtyDays),
                value = formatterUtil.formatCompactNumber(statistics.lastThirtyDays.episodeCount.toLong()),
                caption = localizer.getPlural(PluralsResourceKey.DayCount, statistics.lastThirtyDays.activeDays),
            ),
        )
        add(
            StatisticTile(
                id = StatisticTileId.TitlesTracked,
                label = localizer.getString(StringResourceKey.LabelStatisticsTitlesTracked),
                value = formatterUtil.formatCompactNumber(statistics.showsTracked.total),
                caption = localizer.getString(
                    StringResourceKey.LabelStatisticsCompletedCount,
                    statistics.showsTracked.completed,
                ),
            ),
        )
        add(
            StatisticTile(
                id = StatisticTileId.Episodes,
                label = localizer.getString(StringResourceKey.LabelStatisticsEpisodes),
                value = formatterUtil.formatCompactNumber(statistics.episodesWatched),
                caption = "",
            ),
        )
        statistics.averageRating?.let { rating ->
            add(
                StatisticTile(
                    id = StatisticTileId.AverageRating,
                    label = localizer.getString(StringResourceKey.LabelStatisticsAverageRating),
                    value = formatterUtil.formatDouble(rating.average, RATING_DECIMALS).toString(),
                    caption = localizer.getString(StringResourceKey.LabelStatisticsRatedCount, rating.ratedCount),
                ),
            )
        }
        add(
            StatisticTile(
                id = StatisticTileId.WatchStreak,
                label = localizer.getString(StringResourceKey.LabelStatisticsStreak),
                value = localizer.getPlural(PluralsResourceKey.DayCount, statistics.streak.longestDays),
                caption = localizer.getString(StringResourceKey.LabelStatisticsLongestRun),
            ),
        )
        add(
            StatisticTile(
                id = StatisticTileId.CurrentStreak,
                label = localizer.getString(StringResourceKey.LabelStatisticsCurrentStreak),
                value = localizer.getPlural(PluralsResourceKey.DayCount, statistics.streak.currentDays),
                caption = localizer.getString(StringResourceKey.LabelStatisticsRunningNow),
            ),
        )
    }.toImmutableList()

    public fun toHeatMap(statistics: WatchStatistics): WatchHeatMap? {
        val days = statistics.dailyCounts
        if (days.isEmpty()) return null
        val busiest = days.maxOf { it.episodeCount }
        return WatchHeatMap(
            cells = days.map { day ->
                HeatMap(
                    date = day.date.toString(),
                    level = levelFor(day, busiest),
                    episodeCount = day.episodeCount,
                )
            },
            leadingBlankCells = days.first().date.dayOfWeek.isoDayNumber % DAYS_IN_WEEK,
            activeDays = days.count { it.episodeCount > 0 },
            quietDays = days.count { it.episodeCount == 0 },
            busiestDayCount = busiest,
        )
    }

    public fun toMostWatchedShows(statistics: WatchStatistics): ImmutableList<ShowRowItem> =
        statistics.mostWatchedShows.map { show ->
            ShowRowItem(
                showId = show.showId,
                title = show.title,
                posterPath = show.posterPath,
                caption = localizer.getPlural(PluralsResourceKey.EpisodeCount, show.episodeCount.toInt()),
            )
        }.toImmutableList()

    public fun toHighestRatedShows(statistics: WatchStatistics): ImmutableList<ShowRowItem> =
        statistics.highestRatedShows.map { show ->
            ShowRowItem(
                showId = show.showId,
                title = show.title,
                posterPath = show.posterPath,
                caption = ratingCaption(show.userRating),
            )
        }.toImmutableList()

    private fun ratingCaption(userRating: Long): String {
        val stars = (userRating / POINTS_PER_STAR).toInt()
        return if (userRating % POINTS_PER_STAR == 0L) {
            localizer.getString(StringResourceKey.LabelStatisticsRatingWhole, stars)
        } else {
            localizer.getString(StringResourceKey.LabelStatisticsRatingHalf, stars)
        }
    }

    public fun toWatchStatusBreakdown(statistics: WatchStatistics): ImmutableList<WatchStatusItem> {
        val total = statistics.showsByWatchStatus.sumOf { it.showCount }
        return statistics.showsByWatchStatus.map { entry ->
            WatchStatusItem(
                id = entry.status.toItemId(),
                label = localizer.getString(entry.status.toLabelKey()),
                showCount = entry.showCount,
                fraction = if (total == 0L) 0f else entry.showCount.toFloat() / total,
            )
        }.toImmutableList()
    }

    public fun toRatingBreakdown(statistics: WatchStatistics): ImmutableList<RatingBar> {
        val busiest = statistics.ratingDistribution.maxOfOrNull { it.count } ?: 0L
        return statistics.ratingDistribution.map { entry ->
            RatingBar(
                rating = entry.rating,
                count = entry.count,
                fraction = if (busiest == 0L) 0f else entry.count.toFloat() / busiest,
            )
        }.toImmutableList()
    }

    public fun toYearlyActivity(statistics: WatchStatistics): ImmutableList<ActivityBar> {
        val counts = statistics.yearlyCounts
        if (counts.isEmpty()) return persistentListOf()
        val byYear = counts.associateBy { it.year }
        val lastYear = maxOf(counts.last().year, dateTimeProvider.currentYear())
        return (counts.first().year..lastYear)
            .map { year -> year.toString() to (byYear[year]?.episodeCount ?: 0) }
            .toActivityBars()
    }

    public fun toMonthlyActivity(statistics: WatchStatistics): ImmutableList<ActivityBar> =
        statistics.monthlyCounts
            .map { dateTimeProvider.formatShortMonth(it.month) to it.episodeCount }
            .toActivityBars()

    public fun toWeekdayActivity(statistics: WatchStatistics): ImmutableList<ActivityBar> =
        statistics.weekdayCounts
            .map { dateTimeProvider.formatShortDayOfWeek(it.dayOfWeek) to it.episodeCount }
            .toActivityBars()

    public fun toReleaseYears(statistics: WatchStatistics): ImmutableList<ActivityBar> =
        statistics.releaseYears
            .map { it.year.toString() to it.showCount.toInt() }
            .toActivityBars(PluralsResourceKey.ShowCount)

    public fun toGenreBreakdown(statistics: WatchStatistics): ImmutableList<GenreSlice> {
        val total = statistics.genreBreakdown.sumOf { it.showCount }
        if (total == 0L) return persistentListOf()

        val named = statistics.genreBreakdown.take(NAMED_GENRE_LIMIT)
        val tail = statistics.genreBreakdown.drop(NAMED_GENRE_LIMIT).sumOf { it.showCount }
        val slices = named.map { genre -> genre.name to genre.showCount } +
            if (tail > 0) listOf(localizer.getString(StringResourceKey.LabelStatisticsGenreOther) to tail) else emptyList()

        val biggest = slices.maxOf { (_, showCount) -> showCount }
        return slices.map { (name, showCount) ->
            GenreSlice(
                name = name,
                showCount = showCount.toInt(),
                caption = localizer.getPlural(PluralsResourceKey.ShowCount, showCount.toInt()),
                fraction = showCount.toFloat() / biggest,
            )
        }.toImmutableList()
    }

    public fun toLabels(): StatisticsLabels = StatisticsLabels(
        screenTitle = localizer.getString(StringResourceKey.LabelStatisticsTitle),
        emptyMessage = localizer.getString(StringResourceKey.LabelStatisticsEmpty),
        watchTimeTitle = localizer.getString(StringResourceKey.LabelStatisticsWatchTime),
        daysLabel = localizer.getString(StringResourceKey.LabelStatisticsDays),
        hoursLabel = localizer.getString(StringResourceKey.LabelStatisticsHours),
        minutesLabel = localizer.getString(StringResourceKey.LabelStatisticsMinutes),
        episodesOverTimeTitle = localizer.getString(StringResourceKey.LabelStatisticsActivity),
        markedWatchedNote = localizer.getString(StringResourceKey.LabelStatisticsMarkedWatchedNote),
        mostWatchedTitle = localizer.getString(StringResourceKey.LabelStatisticsMostWatched),
        highestRatedTitle = localizer.getString(StringResourceKey.LabelStatisticsHighestRated),
        watchStatusTitle = localizer.getString(StringResourceKey.LabelStatisticsWatchStatus),
        ratingsTitle = localizer.getString(StringResourceKey.LabelStatisticsRatings),
        yearlyActivityTitle = localizer.getString(StringResourceKey.LabelStatisticsYearlyActivity),
        monthlyActivityTitle = localizer.getString(StringResourceKey.LabelStatisticsMonthlyActivity),
        weekdayActivityTitle = localizer.getString(StringResourceKey.LabelStatisticsWeekdayActivity),
        genresTitle = localizer.getString(StringResourceKey.LabelStatisticsGenres),
        releaseYearsTitle = localizer.getString(StringResourceKey.LabelStatisticsReleaseYears),
        genresEmptyMessage = localizer.getString(StringResourceKey.LabelStatisticsGenresEmpty),
        lockedTitle = localizer.getString(StringResourceKey.LabelStatisticsLockedTitle),
        lockedMessage = localizer.getString(StringResourceKey.LabelStatisticsLockedMessage),
        lockedBadgeText = localizer.getString(StringResourceKey.LabelPremiumBadge),
        lockedActionText = localizer.getString(StringResourceKey.LabelUpgradeToPremium),
        lockedContentDescription = localizer.getString(StringResourceKey.CdLocked),
    )

    private fun List<Pair<String, Int>>.toActivityBars(
        plural: PluralsResourceKey = PluralsResourceKey.EpisodeCount,
    ): ImmutableList<ActivityBar> {
        val busiest = maxOfOrNull { it.second } ?: 0
        return map { (label, count) ->
            ActivityBar(
                label = label,
                count = count,
                caption = localizer.getPlural(plural, count),
                fraction = if (busiest == 0) 0f else count.toFloat() / busiest,
            )
        }.toImmutableList()
    }

    private fun levelFor(day: DailyWatchCount, busiest: Int): Int = when {
        day.episodeCount == 0 -> 0
        busiest <= 0 -> 0
        else -> {
            val share = day.episodeCount.toFloat() / busiest
            val level = kotlin.math.ceil(share * HEAT_MAP_LEVELS).toInt()
            level.coerceIn(1, HEAT_MAP_LEVELS)
        }
    }

    private fun WatchStatus.toItemId(): WatchStatusItemId = when (this) {
        WatchStatus.WATCHLIST -> WatchStatusItemId.Watchlist
        WatchStatus.WATCHING -> WatchStatusItemId.Watching
        WatchStatus.COMPLETED -> WatchStatusItemId.Completed
        WatchStatus.ON_HOLD -> WatchStatusItemId.OnHold
        WatchStatus.DROPPED -> WatchStatusItemId.Dropped
    }

    private fun WatchStatus.toLabelKey(): StringResourceKey = when (this) {
        WatchStatus.WATCHLIST -> StringResourceKey.LabelWatchStatusWatchlist
        WatchStatus.WATCHING -> StringResourceKey.LabelWatchStatusWatching
        WatchStatus.COMPLETED -> StringResourceKey.LabelWatchStatusCompleted
        WatchStatus.ON_HOLD -> StringResourceKey.LabelWatchStatusOnHold
        WatchStatus.DROPPED -> StringResourceKey.LabelWatchStatusDropped
    }

    private companion object {
        const val RATING_DECIMALS = 1
        const val HEAT_MAP_LEVELS = 4
        const val DAYS_IN_WEEK = 7
        const val NAMED_GENRE_LIMIT = 6
        const val POINTS_PER_STAR = 2
    }
}
