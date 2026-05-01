package com.thomaskioko.tvmaniac.ui.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_discover_up_next
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_item_progress
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_calendar
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.presentation.calendar.CalendarAction
import com.thomaskioko.tvmaniac.presentation.calendar.CalendarState
import com.thomaskioko.tvmaniac.presentation.calendar.model.CalendarDateGroup
import com.thomaskioko.tvmaniac.presentation.calendar.model.CalendarEpisodeItem
import com.thomaskioko.tvmaniac.presentation.progress.ProgressAction
import com.thomaskioko.tvmaniac.presentation.progress.ProgressPresenter
import com.thomaskioko.tvmaniac.presentation.progress.ProgressState
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextAction
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextState
import com.thomaskioko.tvmaniac.presentation.upnext.model.UpNextEpisodeUiModel
import com.thomaskioko.tvmaniac.testtags.progress.ProgressTestTags
import com.thomaskioko.tvmaniac.ui.calendar.CalendarPageContent
import com.thomaskioko.tvmaniac.ui.upnext.UpNextPageContent
import kotlinx.collections.immutable.persistentListOf

@Composable
public fun ProgressScreen(
    presenter: ProgressPresenter,
    modifier: Modifier = Modifier,
) {
    val progressState by presenter.state.collectAsState()
    val upNextState by presenter.upNextPresenter.state.collectAsState()
    val calendarState by presenter.calendarPresenter.state.collectAsState()
    val context = LocalContext.current

    val tabs = listOf(
        label_discover_up_next.resolve(context),
        title_calendar.resolve(context),
    )

    ProgressScreen(
        progressState = progressState,
        upNextState = upNextState,
        calendarState = calendarState,
        tabs = tabs,
        modifier = modifier,
        progressAction = presenter::dispatch,
        upNextAction = presenter.upNextPresenter::dispatch,
        calendarAction = presenter.calendarPresenter::dispatch,
    )
}

@Composable
internal fun ProgressScreen(
    progressState: ProgressState,
    upNextState: UpNextState,
    calendarState: CalendarState,
    tabs: List<String>,
    modifier: Modifier,
    progressAction: (ProgressAction) -> Unit,
    upNextAction: (UpNextAction) -> Unit,
    calendarAction: (CalendarAction) -> Unit,
) {
    ProgressScreen(
        selectedPage = progressState.selectedPage,
        isLoading = upNextState.isLoading || calendarState.isLoading,
        tabs = tabs,
        modifier = modifier,
        onSelectPage = { progressAction(ProgressAction.SelectPage(it)) },
        upNextContent = {
            UpNextPageContent(
                state = upNextState,
                modifier = Modifier.fillMaxSize(),
                onAction = upNextAction,
            )
        },
        calendarContent = {
            CalendarPageContent(
                state = calendarState,
                modifier = Modifier.fillMaxSize(),
                onAction = calendarAction,
            )
        },
    )
}

@Composable
internal fun ProgressScreen(
    selectedPage: Int,
    isLoading: Boolean,
    tabs: List<String>,
    modifier: Modifier = Modifier,
    onSelectPage: (Int) -> Unit = {},
    upNextContent: @Composable () -> Unit = {},
    calendarContent: @Composable () -> Unit = {},
) {
    val pagerState = rememberPagerState(
        initialPage = selectedPage,
        pageCount = { tabs.size },
    )

    LaunchedEffect(selectedPage) {
        if (pagerState.currentPage != selectedPage) {
            pagerState.animateScrollToPage(selectedPage)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onSelectPage(page)
        }
    }

    Scaffold(
        modifier = modifier
            .statusBarsPadding()
            .testTag(ProgressTestTags.SCREEN_TEST_TAG),
        topBar = {
            TvManiacTopBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = menu_item_progress.resolve(LocalContext.current),
                            modifier = Modifier.testTag("progress_title"),
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .size(20.dp),
                                color = MaterialTheme.colorScheme.secondary,
                                strokeWidth = 2.dp,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding.copy(copyBottom = false)),
        ) {
            SecondaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("progress_tab_row"),
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(pagerState.currentPage),
                        color = MaterialTheme.colorScheme.secondary,
                    )
                },
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        modifier = Modifier.testTag("progress_tab_$index"),
                        selected = pagerState.currentPage == index,
                        onClick = { onSelectPage(index) },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall,
                            )
                        },
                        selectedContentColor = MaterialTheme.colorScheme.secondary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) { page ->
                when (page) {
                    0 -> upNextContent()
                    1 -> calendarContent()
                }
            }
        }
    }
}

internal data class ProgressPreviewViewState(
    val progressState: ProgressState,
    val upNextState: UpNextState,
    val calendarState: CalendarState,
)

internal class ProgressPreviewParameterProvider : PreviewParameterProvider<ProgressPreviewViewState> {
    override val values: Sequence<ProgressPreviewViewState> = sequenceOf(
        ProgressPreviewViewState(
            progressState = ProgressState(selectedPage = 0),
            upNextState = UpNextState(
                isLoading = false,
                episodes = previewUpNextEpisodes(),
            ),
            calendarState = CalendarState(
                isLoading = false,
                isLoggedIn = true,
                weekLabel = "Jan 31, 2026 - Feb 6, 2026",
                dateGroups = previewCalendarEvents(),
            ),
        ),
        ProgressPreviewViewState(
            progressState = ProgressState(selectedPage = 1),
            upNextState = UpNextState(
                isLoading = false,
                episodes = previewUpNextEpisodes(),
            ),
            calendarState = CalendarState(
                isLoading = false,
                isLoggedIn = true,
                weekLabel = "Jan 31, 2026 - Feb 6, 2026",
                dateGroups = previewCalendarEvents(),
            ),
        ),
    )
}

internal fun previewUpNextEpisodes() = persistentListOf(
    UpNextEpisodeUiModel(
        showTraktId = 1,
        showTmdbId = 1396,
        showName = "Breaking Bad",
        showStatus = "Ended",
        showYear = "2008",
        episodeId = 101,
        episodeName = "Ozymandias",
        seasonId = 10,
        seasonNumber = 5,
        episodeNumber = 14,
        runtime = 47,
        overview = "Everyone copes with radically changed circumstances.",
        firstAired = null,
        seasonCount = 5,
        episodeCount = 62,
        watchedCount = 55,
        totalCount = 62,
        formattedEpisodeNumber = "S05E14",
        remainingEpisodes = 7,
        formattedRuntime = "47m",
        imageUrl = null,
    ),
)

internal fun previewCalendarEvents() = persistentListOf(
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
)

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ProgressScreenPreview(
    @PreviewParameter(ProgressPreviewParameterProvider::class)
    state: ProgressPreviewViewState,
) {
    ProgressScreen(
        progressState = state.progressState,
        upNextState = state.upNextState,
        calendarState = state.calendarState,
        tabs = listOf("Up Next", "Calendar"),
        modifier = Modifier.fillMaxSize(),
        progressAction = {},
        upNextAction = {},
        calendarAction = {},
    )
}
