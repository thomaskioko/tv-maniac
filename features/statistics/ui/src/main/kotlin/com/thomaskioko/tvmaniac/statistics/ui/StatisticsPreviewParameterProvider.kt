package com.thomaskioko.tvmaniac.statistics.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.statistics.presenter.StatisticsLabels
import com.thomaskioko.tvmaniac.statistics.presenter.StatisticsState
import com.thomaskioko.tvmaniac.statistics.presenter.model.MostWatchedShowItem
import com.thomaskioko.tvmaniac.statistics.presenter.model.RatingBar
import com.thomaskioko.tvmaniac.statistics.presenter.model.StatisticTile
import com.thomaskioko.tvmaniac.statistics.presenter.model.StatisticTileId
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
    episodesOverTimeTitle = "Your last year of watching",
    markedWatchedNote = "Dates show when episodes were marked as watched, not when you watched them.",
    mostWatchedTitle = "Most watched shows",
    watchStatusTitle = "Shows by status",
    ratingsTitle = "Your ratings",
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
)

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
