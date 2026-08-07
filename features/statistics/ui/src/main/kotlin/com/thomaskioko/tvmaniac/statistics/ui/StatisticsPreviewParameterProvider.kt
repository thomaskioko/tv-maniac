package com.thomaskioko.tvmaniac.statistics.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.statistics.presenter.StatisticsLabels
import com.thomaskioko.tvmaniac.statistics.presenter.StatisticsState
import com.thomaskioko.tvmaniac.statistics.presenter.model.ActivityBar
import com.thomaskioko.tvmaniac.statistics.presenter.model.HeatMap
import com.thomaskioko.tvmaniac.statistics.presenter.model.MostWatchedShowItem
import com.thomaskioko.tvmaniac.statistics.presenter.model.RatingBar
import com.thomaskioko.tvmaniac.statistics.presenter.model.StatisticTile
import com.thomaskioko.tvmaniac.statistics.presenter.model.StatisticTileId
import com.thomaskioko.tvmaniac.statistics.presenter.model.WatchHeatMap
import com.thomaskioko.tvmaniac.statistics.presenter.model.WatchStatusItem
import com.thomaskioko.tvmaniac.statistics.presenter.model.WatchStatusItemId
import com.thomaskioko.tvmaniac.statistics.presenter.model.WatchTime
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal val previewLabels: StatisticsLabels = StatisticsLabels(
    screenTitle = "Statistics",
    emptyMessage = "Mark an episode as watched to start building your statistics.",
    watchTimeTitle = "Total time watched",
    daysLabel = "days",
    hoursLabel = "hours",
    minutesLabel = "minutes",
    episodesOverTimeTitle = "Your year of watching",
    markedWatchedNote = "Dates show when episodes were marked as watched, not when you watched them.",
    mostWatchedTitle = "Most watched shows",
    watchStatusTitle = "Shows by status",
    ratingsTitle = "Your ratings",
    yearlyActivityTitle = "Episodes by year",
    monthlyActivityTitle = "Episodes by month",
    weekdayActivityTitle = "Episodes by day of the week",
    lockedTitle = "Statistics are a Premium feature",
    lockedMessage = "Upgrade to Premium to see how you watch.",
    lockedBadgeText = "Premium",
    lockedActionText = "Upgrade to Premium",
    lockedContentDescription = "Locked",
)

private val previewTiles: ImmutableList<StatisticTile> = persistentListOf(
    StatisticTile(
        id = StatisticTileId.PeakYear,
        label = "Peak year",
        value = "2023",
        caption = "312 episodes",
    ),
    StatisticTile(
        id = StatisticTileId.WatchDaysThisYear,
        label = "Watch days this year",
        value = "112/214",
        caption = "days you watched something",
    ),
    StatisticTile(
        id = StatisticTileId.WatchDaysThisMonth,
        label = "Watch days this month",
        value = "11/28",
        caption = "days you watched something",
    ),
    StatisticTile(
        id = StatisticTileId.TopWeekday,
        label = "Top weekday",
        value = "Sunday",
        caption = "48 episodes",
    ),
    StatisticTile(
        id = StatisticTileId.LastThirtyDays,
        label = "Last 30 days",
        value = "24",
        caption = "18 days",
    ),
    StatisticTile(
        id = StatisticTileId.TitlesTracked,
        label = "Titles tracked",
        value = "128",
        caption = "18 completed",
    ),
    StatisticTile(
        id = StatisticTileId.Episodes,
        label = "Episodes",
        value = "1.2K",
        caption = "",
    ),
    StatisticTile(
        id = StatisticTileId.AverageRating,
        label = "Average rating",
        value = "8.4",
        caption = "across 42 rated",
    ),
    StatisticTile(
        id = StatisticTileId.WatchStreak,
        label = "Watch streak",
        value = "9 days",
        caption = "longest run",
    ),
    StatisticTile(
        id = StatisticTileId.CurrentStreak,
        label = "Current streak",
        value = "3 days",
        caption = "running now",
    ),
)

private val previewYearlyActivity: ImmutableList<ActivityBar> = persistentListOf(
    ActivityBar(label = "2022", episodeCount = 84, caption = "84 episodes", fraction = 0.27f),
    ActivityBar(label = "2023", episodeCount = 312, caption = "312 episodes", fraction = 1f),
    ActivityBar(label = "2024", episodeCount = 196, caption = "196 episodes", fraction = 0.63f),
    ActivityBar(label = "2025", episodeCount = 148, caption = "148 episodes", fraction = 0.47f),
    ActivityBar(label = "2026", episodeCount = 91, caption = "91 episodes", fraction = 0.29f),
)

private val previewMonthlyActivity: ImmutableList<ActivityBar> = persistentListOf(
    ActivityBar(label = "Jan", episodeCount = 24, caption = "24 episodes", fraction = 0.6f),
    ActivityBar(label = "Feb", episodeCount = 18, caption = "18 episodes", fraction = 0.45f),
    ActivityBar(label = "Mar", episodeCount = 31, caption = "31 episodes", fraction = 0.77f),
    ActivityBar(label = "Apr", episodeCount = 12, caption = "12 episodes", fraction = 0.3f),
    ActivityBar(label = "May", episodeCount = 40, caption = "40 episodes", fraction = 1f),
    ActivityBar(label = "Jun", episodeCount = 22, caption = "22 episodes", fraction = 0.55f),
    ActivityBar(label = "Jul", episodeCount = 8, caption = "8 episodes", fraction = 0.2f),
    ActivityBar(label = "Aug", episodeCount = 27, caption = "27 episodes", fraction = 0.67f),
    ActivityBar(label = "Sep", episodeCount = 15, caption = "15 episodes", fraction = 0.37f),
    ActivityBar(label = "Oct", episodeCount = 33, caption = "33 episodes", fraction = 0.82f),
    ActivityBar(label = "Nov", episodeCount = 19, caption = "19 episodes", fraction = 0.47f),
    ActivityBar(label = "Dec", episodeCount = 29, caption = "29 episodes", fraction = 0.72f),
)

private val previewWeekdayActivity: ImmutableList<ActivityBar> = persistentListOf(
    ActivityBar(label = "Mon", episodeCount = 12, caption = "12 episodes", fraction = 0.6f),
    ActivityBar(label = "Tue", episodeCount = 8, caption = "8 episodes", fraction = 0.4f),
    ActivityBar(label = "Wed", episodeCount = 14, caption = "14 episodes", fraction = 0.7f),
    ActivityBar(label = "Thu", episodeCount = 6, caption = "6 episodes", fraction = 0.3f),
    ActivityBar(label = "Fri", episodeCount = 17, caption = "17 episodes", fraction = 0.85f),
    ActivityBar(label = "Sat", episodeCount = 20, caption = "20 episodes", fraction = 1f),
    ActivityBar(label = "Sun", episodeCount = 16, caption = "16 episodes", fraction = 0.8f),
)

private val previewHeatMap: WatchHeatMap = WatchHeatMap(
    cells = List(HEAT_MAP_PREVIEW_DAYS) { index ->
        HeatMap(
            date = "2026-01-01",
            level = (index * 7) % 5,
            episodeCount = (index * 7) % 5,
        )
    },
    leadingBlankCells = 3,
    activeDays = 138,
    quietDays = 77,
)

private const val HEAT_MAP_PREVIEW_DAYS = 215

private val previewMostWatchedShows: ImmutableList<MostWatchedShowItem> = persistentListOf(
    MostWatchedShowItem(
        showId = 1396,
        title = "Breaking Bad",
        posterPath = null,
        episodeCount = 62,
        caption = "62 episodes",
    ),
    MostWatchedShowItem(
        showId = 1399,
        title = "Game of Thrones",
        posterPath = null,
        episodeCount = 73,
        caption = "73 episodes",
    ),
    MostWatchedShowItem(
        showId = 66732,
        title = "Stranger Things",
        posterPath = null,
        episodeCount = 34,
        caption = "34 episodes",
    ),
)

private val previewWatchStatusBreakdown: ImmutableList<WatchStatusItem> = persistentListOf(
    WatchStatusItem(id = WatchStatusItemId.Watchlist, label = "Watchlist", showCount = 24, fraction = 0.4f),
    WatchStatusItem(id = WatchStatusItemId.Watching, label = "Watching", showCount = 6, fraction = 0.1f),
    WatchStatusItem(id = WatchStatusItemId.Completed, label = "Completed", showCount = 28, fraction = 0.46f),
    WatchStatusItem(id = WatchStatusItemId.OnHold, label = "On hold", showCount = 2, fraction = 0.03f),
    WatchStatusItem(id = WatchStatusItemId.Dropped, label = "Dropped", showCount = 1, fraction = 0.01f),
)

private val previewRatingBreakdown: ImmutableList<RatingBar> = persistentListOf(
    RatingBar(rating = 10, count = 12, fraction = 1f),
    RatingBar(rating = 9, count = 8, fraction = 0.66f),
    RatingBar(rating = 8, count = 10, fraction = 0.83f),
    RatingBar(rating = 7, count = 3, fraction = 0.25f),
    RatingBar(rating = 6, count = 1, fraction = 0.08f),
)

internal val loadingState: StatisticsState = StatisticsState(
    isLoading = true,
    labels = previewLabels,
)

internal val emptyState: StatisticsState = StatisticsState(
    isLoading = false,
    hasWatchHistory = false,
    labels = previewLabels,
)

internal val contentState: StatisticsState = StatisticsState(
    isLoading = false,
    hasWatchHistory = true,
    totalWatchTime = WatchTime(days = 12, hours = 4, minutes = 30),
    tiles = previewTiles,
    mostWatchedShows = previewMostWatchedShows,
    watchStatusBreakdown = previewWatchStatusBreakdown,
    ratingBreakdown = previewRatingBreakdown,
    yearlyActivity = previewYearlyActivity,
    monthlyActivity = previewMonthlyActivity,
    weekdayActivity = previewWeekdayActivity,
    heatMap = previewHeatMap,
    labels = previewLabels,
)

internal val markedWatchedTimesState: StatisticsState = contentState.copy(
    showsMarkedWatchedTimes = true,
)

internal val lockedState: StatisticsState = contentState.copy(
    isLocked = true,
)

internal class StatisticsPreviewParameterProvider : PreviewParameterProvider<StatisticsState> {
    override val values: Sequence<StatisticsState>
        get() = sequenceOf(
            loadingState,
            emptyState,
            contentState,
            markedWatchedTimesState,
            lockedState,
        )
}
