package com.thomaskioko.tvmaniac.ui.upnext

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.EmptyContent
import com.thomaskioko.tvmaniac.compose.components.SelectableFilterChip
import com.thomaskioko.tvmaniac.compose.components.SnackBarStyle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacSnackBarHost
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.domain.upnext.model.UpNextSortOption
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_discover_up_next
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_upnext_empty
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_upnext_sort_air_date
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_upnext_sort_last_watched
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.presentation.upnext.MarkWatched
import com.thomaskioko.tvmaniac.presentation.upnext.RefreshUpNext
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextAction
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextChangeSortOption
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextMessageShown
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextPresenter
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextShowClicked
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextState
import com.thomaskioko.tvmaniac.ui.upnext.preview.UpNextStatePreviewParameterProvider

@Composable
public fun UpNextScreen(
    presenter: UpNextPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsState()

    UpNextScreen(
        modifier = modifier,
        state = state,
        onAction = presenter::dispatch,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun UpNextScreen(
    state: UpNextState,
    modifier: Modifier = Modifier,
    onAction: (UpNextAction) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()
    val context = LocalContext.current

    LaunchedEffect(state.episodes.firstOrNull()?.showTraktId, state.sortOption) {
        listState.animateScrollToItem(0)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = modifier.statusBarsPadding(),
            topBar = {
                TvManiacTopBar(
                    title = { TopBarContent(state = state) },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = MaterialTheme.colorScheme.background,
                    ),
                )
            },
            content = { contentPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding.copy(copyBottom = false)),
                ) {
                    SortChipsRow(
                        currentSortOption = state.sortOption,
                        onSortOptionSelected = { onAction(UpNextChangeSortOption(it)) },
                    )

                    PullToRefreshBox(
                        isRefreshing = state.isRefreshing,
                        onRefresh = { onAction(RefreshUpNext) },
                        modifier = Modifier.weight(1f),
                    ) {
                        when {
                            state.showLoading -> Box(modifier = Modifier.fillMaxSize())
                            state.isEmpty -> {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    item {
                                        EmptyContent(
                                            imageVector = Icons.Outlined.Inbox,
                                            message = label_upnext_empty.resolve(context),
                                        )
                                    }
                                }
                            }
                            else -> {
                                LazyColumn(
                                    state = listState,
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                                ) {
                                    items(
                                        items = state.episodes,
                                        key = { it.showTraktId },
                                    ) { episode ->
                                        UpNextListItem(
                                            modifier = Modifier.animateItem(),
                                            item = episode,
                                            onItemClicked = { onAction(UpNextShowClicked(it)) },
                                            onMarkWatched = {
                                                onAction(
                                                    MarkWatched(
                                                        showTraktId = episode.showTraktId,
                                                        episodeId = episode.episodeId!!,
                                                        seasonNumber = episode.seasonNumber!!,
                                                        episodeNumber = episode.episodeNumber!!,
                                                    ),
                                                )
                                            },
                                        )
                                    }

                                    item {
                                        Spacer(modifier = Modifier.navigationBarsPadding())
                                    }
                                }
                            }
                        }
                    }
                }
            },
        )

        TvManiacSnackBarHost(
            message = state.message?.message,
            style = SnackBarStyle.Error,
            onDismiss = { state.message?.let { onAction(UpNextMessageShown(it.id)) } },
        )
    }
}

@Composable
private fun TopBarContent(
    state: UpNextState,
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label_discover_up_next.resolve(context),
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(20.dp),
                color = MaterialTheme.colorScheme.secondary,
                strokeWidth = 2.dp,
            )
        }
    }
}

@Composable
private fun SortChipsRow(
    currentSortOption: UpNextSortOption,
    onSortOptionSelected: (UpNextSortOption) -> Unit,
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SelectableFilterChip(
            label = label_upnext_sort_last_watched.resolve(context),
            isSelected = currentSortOption == UpNextSortOption.LAST_WATCHED,
            onClick = { onSortOptionSelected(UpNextSortOption.LAST_WATCHED) },
        )
        SelectableFilterChip(
            label = label_upnext_sort_air_date.resolve(context),
            isSelected = currentSortOption == UpNextSortOption.AIR_DATE,
            onClick = { onSortOptionSelected(UpNextSortOption.AIR_DATE) },
        )
    }
}

@ThemePreviews
@Composable
private fun UpNextScreenPreview(
    @PreviewParameter(UpNextStatePreviewParameterProvider::class) state: UpNextState,
) {
    TvManiacTheme {
        UpNextScreen(
            state = state,
            onAction = {},
        )
    }
}
