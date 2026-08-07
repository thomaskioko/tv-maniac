package com.thomaskioko.tvmaniac.statistics.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.thomaskioko.tvmaniac.compose.components.EmptyStateView
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.PremiumOverlay
import com.thomaskioko.tvmaniac.compose.components.SnackBarStyle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.components.TvManiacSnackBarHost
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_back
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.statistics.presenter.StatisticsAction
import com.thomaskioko.tvmaniac.statistics.presenter.StatisticsPresenter
import com.thomaskioko.tvmaniac.statistics.presenter.StatisticsState
import com.thomaskioko.tvmaniac.statistics.ui.components.ActivityChartSection
import com.thomaskioko.tvmaniac.statistics.ui.components.MostWatchedShowsSection
import com.thomaskioko.tvmaniac.statistics.ui.components.RatingsSection
import com.thomaskioko.tvmaniac.statistics.ui.components.StatisticTileGrid
import com.thomaskioko.tvmaniac.statistics.ui.components.WatchHeatMapSection
import com.thomaskioko.tvmaniac.statistics.ui.components.WatchStatusSection
import com.thomaskioko.tvmaniac.statistics.ui.components.WatchTimeSection
import com.thomaskioko.tvmaniac.testtags.statistics.StatisticsTestTags
import io.github.thomaskioko.codegen.annotations.ScreenUi

@ScreenUi(presenter = StatisticsPresenter::class, parentScope = ActivityScope::class)
@Composable
public fun StatisticsScreen(
    presenter: StatisticsPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsState()

    StatisticsScreen(
        state = state,
        onAction = presenter::dispatch,
        modifier = modifier,
    )
}

@Composable
internal fun StatisticsScreen(
    state: StatisticsState,
    onAction: (StatisticsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.testTag(StatisticsTestTags.SCREEN_TEST_TAG),
        topBar = {
            TvManiacTopBar(
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .clickable(onClick = { onAction(StatisticsAction.BackClicked) })
                            .padding(TvManiacSpacing.medium)
                            .testTag(StatisticsTestTags.BACK_BUTTON_TEST_TAG),
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = cd_back.resolve(context),
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                },
                title = {
                    Text(
                        text = state.labels.screenTitle,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = TvManiacSpacing.medium),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { innerPadding ->
        StatisticsBody(
            state = state,
            contentPadding = innerPadding,
            onAction = onAction,
        )

        TvManiacSnackBarHost(
            message = state.message?.message,
            style = SnackBarStyle.Error,
            onDismiss = { state.message?.let { onAction(StatisticsAction.MessageShown(it.id)) } },
        )
    }
}

@Composable
private fun StatisticsBody(
    state: StatisticsState,
    contentPadding: PaddingValues,
    onAction: (StatisticsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLocked) {
        PremiumOverlay(
            locked = true,
            badgeText = state.labels.lockedBadgeText,
            title = state.labels.lockedTitle,
            message = state.labels.lockedMessage,
            actionText = state.labels.lockedActionText,
            onActionClick = { onAction(StatisticsAction.UpgradeClicked) },
            modifier = modifier
                .fillMaxSize()
                .testTag(StatisticsTestTags.LOCKED_STATE_TEST_TAG),
        ) {
            StatisticsPageBody(state = state, contentPadding = contentPadding, onAction = onAction)
        }
    } else {
        StatisticsPageBody(state = state, contentPadding = contentPadding, onAction = onAction, modifier = modifier)
    }
}

@Composable
private fun StatisticsPageBody(
    state: StatisticsState,
    contentPadding: PaddingValues,
    onAction: (StatisticsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.isLoading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .testTag(StatisticsTestTags.LOADING_INDICATOR_TEST_TAG),
                contentAlignment = Alignment.Center,
            ) {
                LoadingIndicator()
            }
        }

        state.showEmptyState -> {
            EmptyStateView(
                modifier = modifier
                    .padding(contentPadding)
                    .testTag(StatisticsTestTags.EMPTY_STATE_TEST_TAG),
                imageVector = Icons.Outlined.Inbox,
                title = state.labels.emptyMessage,
            )
        }

        state.showContent -> {
            StatisticsContent(
                state = state,
                contentPadding = contentPadding,
                onAction = onAction,
                modifier = modifier,
            )
        }

        else -> Unit
    }
}

@Composable
private fun StatisticsContent(
    state: StatisticsState,
    contentPadding: PaddingValues,
    onAction: (StatisticsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag(StatisticsTestTags.CONTENT_TEST_TAG),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.large),
    ) {
        state.totalWatchTime?.let { watchTime ->
            item(key = "watch_time_hero") {
                WatchTimeSection(
                    watchTime = watchTime,
                    title = state.labels.watchTimeTitle,
                    daysLabel = state.labels.daysLabel,
                    hoursLabel = state.labels.hoursLabel,
                    minutesLabel = state.labels.minutesLabel,
                    modifier = Modifier.padding(top = TvManiacSpacing.small),
                )
            }
        }

        if (state.tiles.isNotEmpty()) {
            item(key = "tile_grid") {
                StatisticTileGrid(tiles = state.tiles)
            }
        }

        if (state.showsMarkedWatchedTimes) {
            item(key = "marked_watched_note") {
                Text(
                    text = state.labels.markedWatchedNote,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = TvManiacSpacing.medium)
                        .testTag(StatisticsTestTags.MARKED_WATCHED_NOTE_TEST_TAG),
                )
            }
        }

        state.heatMap?.let { heatMap ->
            item(key = "heat_map") {
                WatchHeatMapSection(
                    heatMap = heatMap,
                    title = state.labels.episodesOverTimeTitle,
                )
            }
        }

        if (state.yearlyActivity.isNotEmpty()) {
            item(key = "yearly_activity") {
                ActivityChartSection(
                    bars = state.yearlyActivity,
                    title = state.labels.yearlyActivityTitle,
                    sectionTestTag = StatisticsTestTags.YEARLY_ACTIVITY_TEST_TAG,
                    sectionName = "yearly",
                )
            }
        }

        if (state.monthlyActivity.isNotEmpty()) {
            item(key = "monthly_activity") {
                ActivityChartSection(
                    bars = state.monthlyActivity,
                    title = state.labels.monthlyActivityTitle,
                    sectionTestTag = StatisticsTestTags.MONTHLY_ACTIVITY_TEST_TAG,
                    sectionName = "monthly",
                )
            }
        }

        if (state.weekdayActivity.isNotEmpty()) {
            item(key = "weekday_activity") {
                ActivityChartSection(
                    bars = state.weekdayActivity,
                    title = state.labels.weekdayActivityTitle,
                    sectionTestTag = StatisticsTestTags.WEEKDAY_ACTIVITY_TEST_TAG,
                    sectionName = "weekday",
                )
            }
        }

        if (state.mostWatchedShows.isNotEmpty()) {
            item(key = "most_watched") {
                MostWatchedShowsSection(
                    shows = state.mostWatchedShows,
                    title = state.labels.mostWatchedTitle,
                    onShowClick = { onAction(StatisticsAction.ShowClicked(it)) },
                )
            }
        }

        if (state.watchStatusBreakdown.isNotEmpty()) {
            item(key = "watch_status") {
                WatchStatusSection(
                    items = state.watchStatusBreakdown,
                    title = state.labels.watchStatusTitle,
                )
            }
        }

        if (state.ratingBreakdown.isNotEmpty()) {
            item(key = "ratings") {
                RatingsSection(
                    ratings = state.ratingBreakdown,
                    title = state.labels.ratingsTitle,
                )
            }
        }

        item(key = "bottom_spacer") {
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun StatisticsScreenPreview(
    @PreviewParameter(StatisticsPreviewParameterProvider::class) state: StatisticsState,
) {
    StatisticsScreen(
        state = state,
        onAction = {},
    )
}
