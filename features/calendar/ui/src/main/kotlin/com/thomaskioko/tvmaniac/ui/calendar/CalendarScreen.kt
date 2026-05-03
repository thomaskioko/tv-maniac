package com.thomaskioko.tvmaniac.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.EmptyStateView
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.SnackBarStyle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.components.TvManiacSnackBarHost
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_next_week
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_previous_week
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.presentation.calendar.CalendarAction
import com.thomaskioko.tvmaniac.presentation.calendar.CalendarPresenter
import com.thomaskioko.tvmaniac.presentation.calendar.CalendarState
import com.thomaskioko.tvmaniac.presentation.calendar.EpisodeCardClicked
import com.thomaskioko.tvmaniac.presentation.calendar.MessageShown
import com.thomaskioko.tvmaniac.presentation.calendar.NavigateToNextWeek
import com.thomaskioko.tvmaniac.presentation.calendar.NavigateToPreviousWeek
import com.thomaskioko.tvmaniac.presentation.calendar.model.CalendarDateGroup
import com.thomaskioko.tvmaniac.presentation.calendar.model.CalendarEpisodeItem
import com.thomaskioko.tvmaniac.testtags.calendar.CalendarTestTags
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
public fun CalendarScreen(
    presenter: CalendarPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsState()

    CalendarScreen(
        modifier = modifier,
        state = state,
        onAction = presenter::dispatch,
    )

    TvManiacSnackBarHost(
        message = state.message?.message,
        style = SnackBarStyle.Error,
        onDismiss = { state.message?.let { presenter.dispatch(MessageShown(it.id)) } },
    )
}

@Composable
public fun CalendarPageContent(
    state: CalendarState,
    modifier: Modifier = Modifier,
    onAction: (CalendarAction) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            WeekNavigationHeader(
                weekLabel = state.weekLabel,
                canNavigatePrevious = state.canNavigatePrevious,
                canNavigateNext = state.canNavigateNext,
                isRefreshing = state.isRefreshing,
                onPreviousClick = { onAction(NavigateToPreviousWeek) },
                onNextClick = { onAction(NavigateToNextWeek) },
                modifier = Modifier.padding(horizontal = 4.dp),
            )

            CalendarBody(
                state = state,
                contentPadding = PaddingValues(0.dp),
                scrollBehavior = scrollBehavior,
                onAction = onAction,
            )
        }

        TvManiacSnackBarHost(
            message = state.message?.message,
            style = SnackBarStyle.Error,
            onDismiss = { state.message?.let { onAction(MessageShown(it.id)) } },
        )
    }
}

@Composable
internal fun CalendarScreen(
    state: CalendarState,
    modifier: Modifier = Modifier,
    onAction: (CalendarAction) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.statusBarsPadding(),
        topBar = {
            TvManiacTopBar(
                title = {
                    WeekNavigationHeader(
                        weekLabel = state.weekLabel,
                        canNavigatePrevious = state.canNavigatePrevious,
                        canNavigateNext = state.canNavigateNext,
                        isRefreshing = state.isRefreshing,
                        onPreviousClick = { onAction(NavigateToPreviousWeek) },
                        onNextClick = { onAction(NavigateToNextWeek) },
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { contentPadding ->
        CalendarBody(
            state = state,
            contentPadding = contentPadding,
            scrollBehavior = scrollBehavior,
            onAction = onAction,
        )
    }
}

@Composable
private fun CalendarBody(
    state: CalendarState,
    contentPadding: PaddingValues,
    scrollBehavior: androidx.compose.material3.TopAppBarScrollBehavior,
    onAction: (CalendarAction) -> Unit,
) {
    when {
        state.showLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(CalendarTestTags.LOADING_INDICATOR)
                    .padding(contentPadding),
                contentAlignment = Alignment.Center,
            ) {
                LoadingIndicator()
            }
        }

        !state.isLoggedIn -> {
            EmptyStateView(
                modifier = Modifier
                    .padding(contentPadding)
                    .testTag(CalendarTestTags.LOGGED_OUT_STATE_TEST_TAG),
                imageVector = Icons.Outlined.CalendarMonth,
                title = state.loginTitle,
                message = state.loginMessage,
            )
        }

        state.isEmpty -> {
            EmptyStateView(
                modifier = Modifier
                    .padding(contentPadding)
                    .testTag(CalendarTestTags.EMPTY_STATE_TEST_TAG),
                imageVector = Icons.Outlined.CalendarMonth,
                title = state.emptyTitle,
                message = state.emptyMessage,
            )
        }

        else -> {
            CalendarContent(
                modifier = Modifier.testTag(CalendarTestTags.SCREEN_TEST_TAG),
                dateGroups = state.dateGroups,
                moreEpisodesFormat = state.moreEpisodesFormat,
                contentPadding = contentPadding,
                scrollBehavior = scrollBehavior,
                onEpisodeClicked = { episodeTraktId -> onAction(EpisodeCardClicked(episodeTraktId)) },
            )
        }
    }
}

@Composable
internal fun WeekNavigationHeader(
    weekLabel: String,
    canNavigatePrevious: Boolean,
    canNavigateNext: Boolean,
    isRefreshing: Boolean,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(
            modifier = Modifier.testTag(CalendarTestTags.PREVIOUS_WEEK_BUTTON),
            onClick = onPreviousClick,
            enabled = canNavigatePrevious,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = cd_previous_week.resolve(context),
                tint = if (canNavigatePrevious) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                },
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                modifier = Modifier.testTag(CalendarTestTags.WEEK_LABEL),
                text = weekLabel,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            if (isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }

        IconButton(
            modifier = Modifier.testTag(CalendarTestTags.NEXT_WEEK_BUTTON),
            onClick = onNextClick,
            enabled = canNavigateNext,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = cd_next_week.resolve(context),
                tint = if (canNavigateNext) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                },
            )
        }
    }
}

@Composable
private fun CalendarContent(
    dateGroups: ImmutableList<CalendarDateGroup>,
    moreEpisodesFormat: String,
    contentPadding: PaddingValues,
    scrollBehavior: androidx.compose.material3.TopAppBarScrollBehavior,
    onEpisodeClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        dateGroups.forEachIndexed { index, dateGroup ->
            item(key = dateGroup.dateLabel) {
                CalendarDateHeader(
                    dateLabel = dateGroup.dateLabel,
                    modifier = if (index == 0) Modifier.padding(top = 8.dp) else Modifier,
                )
            }

            items(
                items = dateGroup.episodes,
                key = { "${dateGroup.dateLabel}_${it.showTraktId}" },
            ) { episode ->
                CalendarEpisodeCard(
                    modifier = Modifier.testTag(CalendarTestTags.episodeCard(episode.episodeTraktId)),
                    episode = episode,
                    moreEpisodesFormat = moreEpisodesFormat,
                    onClick = { onEpisodeClicked(episode.episodeTraktId) },
                )
            }
        }

        item {
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun CalendarDateHeader(
    dateLabel: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag(CalendarTestTags.dateHeader(dateLabel)),
        text = dateLabel,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun CalendarEpisodeCard(
    episode: CalendarEpisodeItem,
    moreEpisodesFormat: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PosterCard(
                    imageUrl = episode.posterUrl,
                    onClick = onClick,
                    modifier = Modifier.fillMaxHeight(),
                    imageWidth = 90.dp,
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 12.dp)
                        .padding(end = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = episode.showTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = episode.episodeInfo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    episode.airTime?.let { airTime ->
                        val airTimeText = if (episode.network != null) {
                            "$airTime on ${episode.network}"
                        } else {
                            airTime
                        }
                        Text(
                            text = airTimeText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        )
                    }
                }
            }

            if (episode.additionalEpisodesCount > 0) {
                Box(
                    modifier = Modifier
                        .testTag(CalendarTestTags.additionalEpisodesCount(episode.episodeTraktId))
                        .semantics(mergeDescendants = true) {}
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = moreEpisodesFormat.format(episode.additionalEpisodesCount),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun CalendarScreenPreview() {
    CalendarScreen(
        state = CalendarState(
            isLoading = false,
            isLoggedIn = true,
            weekLabel = "Jan 31, 2026 - Feb 6, 2026",
            canNavigatePrevious = false,
            moreEpisodesFormat = "+%d episodes",
            dateGroups = persistentListOf(
                CalendarDateGroup(
                    dateLabel = "Today, Jan 31, 2026",
                    episodes = persistentListOf(
                        CalendarEpisodeItem(
                            showTraktId = 1,
                            episodeTraktId = 100,
                            showTitle = "Severance",
                            posterUrl = null,
                            episodeInfo = "S02E01 · Hello, Ms. Cobel",
                            airTime = "03:00",
                            network = "Apple TV+",
                            additionalEpisodesCount = 0,
                            overview = "Mark leads the team on a new mission.",
                            rating = 8.5,
                            votes = 120,
                            runtime = 50,
                            formattedAirDate = "Friday, January 31, 2026 at 03:00",
                        ),
                    ),
                ),
                CalendarDateGroup(
                    dateLabel = "Tomorrow, Feb 1, 2026",
                    episodes = persistentListOf(
                        CalendarEpisodeItem(
                            showTraktId = 2,
                            episodeTraktId = 200,
                            showTitle = "Hell's Paradise",
                            posterUrl = null,
                            episodeInfo = "S02E04 · The Battle Begins",
                            airTime = "15:45",
                            network = null,
                            additionalEpisodesCount = 1,
                            overview = null,
                            rating = null,
                            votes = null,
                            runtime = 24,
                            formattedAirDate = "Saturday, February 1, 2026 at 15:45",
                        ),
                    ),
                ),
            ),
        ),
        onAction = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun CalendarScreenEmptyPreview() {
    CalendarScreen(
        state = CalendarState(
            isLoading = false,
            isLoggedIn = true,
            weekLabel = "Jan 31, 2026 - Feb 6, 2026",
            emptyTitle = "Nothing to see here",
            emptyMessage = "No upcoming episodes",
        ),
        onAction = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun CalendarScreenNotLoggedInPreview() {
    CalendarScreen(
        state = CalendarState(
            isLoading = false,
            isLoggedIn = false,
            weekLabel = "Jan 31, 2026 - Feb 6, 2026",
            canNavigateNext = false,
            loginTitle = "Nothing to see here",
            loginMessage = "Login to Trakt to see your calendar",
        ),
        onAction = {},
    )
}
