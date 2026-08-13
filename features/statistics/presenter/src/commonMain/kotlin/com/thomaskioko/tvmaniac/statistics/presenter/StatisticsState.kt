package com.thomaskioko.tvmaniac.statistics.presenter

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.statistics.presenter.model.ActivityBar
import com.thomaskioko.tvmaniac.statistics.presenter.model.GenreSlice
import com.thomaskioko.tvmaniac.statistics.presenter.model.RatingBar
import com.thomaskioko.tvmaniac.statistics.presenter.model.ShowRowItem
import com.thomaskioko.tvmaniac.statistics.presenter.model.StatisticTile
import com.thomaskioko.tvmaniac.statistics.presenter.model.WatchHeatMap
import com.thomaskioko.tvmaniac.statistics.presenter.model.WatchStatusItem
import com.thomaskioko.tvmaniac.statistics.presenter.model.WatchTime
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class StatisticsState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isLocked: Boolean = false,
    val hasWatchHistory: Boolean = false,
    val showsMarkedWatchedTimes: Boolean = false,
    val totalWatchTime: WatchTime? = null,
    val tiles: ImmutableList<StatisticTile> = persistentListOf(),
    val heatMap: WatchHeatMap? = null,
    val mostWatchedShows: ImmutableList<ShowRowItem> = persistentListOf(),
    val highestRatedShows: ImmutableList<ShowRowItem> = persistentListOf(),
    val watchStatusBreakdown: ImmutableList<WatchStatusItem> = persistentListOf(),
    val ratingBreakdown: ImmutableList<RatingBar> = persistentListOf(),
    val yearlyActivity: ImmutableList<ActivityBar> = persistentListOf(),
    val monthlyActivity: ImmutableList<ActivityBar> = persistentListOf(),
    val weekdayActivity: ImmutableList<ActivityBar> = persistentListOf(),
    val genreBreakdown: ImmutableList<GenreSlice> = persistentListOf(),
    val releaseYears: ImmutableList<ActivityBar> = persistentListOf(),
    val labels: StatisticsLabels = StatisticsLabels(),
    val message: UiMessage? = null,
) {
    val showEmptyState: Boolean
        get() = !isLoading && !isLocked && !hasWatchHistory

    val showContent: Boolean
        get() = !isLoading && hasWatchHistory
}
