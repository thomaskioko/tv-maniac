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
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.EmptyStateView
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.presentation.calendar.CalendarAction
import com.thomaskioko.tvmaniac.presentation.calendar.CalendarPresenter
import com.thomaskioko.tvmaniac.presentation.calendar.CalendarState
import com.thomaskioko.tvmaniac.presentation.calendar.EpisodeCardClicked
import com.thomaskioko.tvmaniac.presentation.calendar.EpisodeDetailDismissed
import com.thomaskioko.tvmaniac.presentation.calendar.NavigateToNextWeek
import com.thomaskioko.tvmaniac.presentation.calendar.NavigateToPreviousWeek
import com.thomaskioko.tvmaniac.presentation.calendar.model.CalendarDateGroup
import com.thomaskioko.tvmaniac.presentation.calendar.model.CalendarEpisodeItem
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
}

@Composable
public fun CalendarPageContent(
    state: CalendarState,
    modifier: Modifier = Modifier,
    onAction: (CalendarAction) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(modifier = modifier.fillMaxSize()) {
        WeekNavigationHeader(
            weekLabel = state.weekLabel,
            canNavigatePrevious = state.canNavigatePrevious,
            canNavigateNext = state.canNavigateNext,
            isRefreshing = state.isRefreshing,
            onPreviousClick = { onAction(NavigateToPreviousWeek) },
            onNextClick = { onAction(NavigateToNextWeek) },
            modifier = Modifier.padding(horizontal = 4.dp),
        )

        when {
            state.showLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    LoadingIndicator()
                }
            }

            !state.isLoggedIn -> {
                EmptyStateView(
                    imageVector = Icons.Outlined.CalendarMonth,
                    title = state.loginTitle,
                    message = state.loginMessage,
                )
            }

            state.isEmpty -> {
                EmptyStateView(
                    imageVector = Icons.Outlined.CalendarMonth,
                    title = state.emptyTitle,
                    message = state.emptyMessage,
                )
            }

            else -> {
                CalendarContent(
                    dateGroups = state.dateGroups,
                    moreEpisodesFormat = state.moreEpisodesFormat,
                    contentPadding = PaddingValues(0.dp),
                    scrollBehavior = scrollBehavior,
                    onEpisodeClicked = { episodeTraktId -> onAction(EpisodeCardClicked(episodeTraktId)) },
                )
            }
        }
    }

    state.selectedEpisode?.let { episode ->
        EpisodeDetailBottomSheet(
            episode = episode,
            sheetState = sheetState,
            onDismiss = { onAction(EpisodeDetailDismissed) },
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
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
        when {
            state.showLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    LoadingIndicator()
                }
            }

            !state.isLoggedIn -> {
                EmptyStateView(
                    modifier = Modifier.padding(contentPadding),
                    imageVector = Icons.Outlined.CalendarMonth,
                    title = state.loginTitle,
                    message = state.loginMessage,
                )
            }

            state.isEmpty -> {
                EmptyStateView(
                    modifier = Modifier.padding(contentPadding),
                    imageVector = Icons.Outlined.CalendarMonth,
                    title = state.emptyTitle,
                    message = state.emptyMessage,
                )
            }

            else -> {
                CalendarContent(
                    dateGroups = state.dateGroups,
                    moreEpisodesFormat = state.moreEpisodesFormat,
                    contentPadding = contentPadding,
                    scrollBehavior = scrollBehavior,
                    onEpisodeClicked = { episodeTraktId -> onAction(EpisodeCardClicked(episodeTraktId)) },
                )
            }
        }
    }

    state.selectedEpisode?.let { episode ->
        EpisodeDetailBottomSheet(
            episode = episode,
            sheetState = sheetState,
            onDismiss = { onAction(EpisodeDetailDismissed) },
        )
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
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(
            onClick = onPreviousClick,
            enabled = canNavigatePrevious,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous week",
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
            onClick = onNextClick,
            enabled = canNavigateNext,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next week",
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
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
                    modifier = Modifier.fillMaxHeight(),
                    imageUrl = episode.posterUrl,
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

@Composable
internal fun EpisodeDetailBottomSheet(
    episode: CalendarEpisodeItem,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    com.thomaskioko.tvmaniac.compose.components.EpisodeDetailBottomSheet(
        episode = com.thomaskioko.tvmaniac.compose.components.EpisodeDetailInfo(
            title = episode.showTitle,
            imageUrl = episode.posterUrl,
            episodeInfo = buildString {
                append(episode.episodeInfo)
                episode.formattedAirDate?.let { append(" \u2022 $it") }
            },
            overview = episode.overview,
            rating = episode.rating,
            voteCount = episode.votes?.toLong(),
        ),
        sheetState = sheetState,
        onDismiss = onDismiss,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun CalendarScreenPreview() {
    TvManiacTheme {
        Surface {
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
    }
}

@Preview
@Composable
private fun CalendarScreenEmptyPreview() {
    TvManiacTheme {
        Surface {
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
    }
}

@Preview
@Composable
private fun CalendarScreenNotLoggedInPreview() {
    TvManiacTheme {
        Surface {
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
    }
}
