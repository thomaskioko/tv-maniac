package com.thomaskioko.tvmaniac.myshows.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.EmptyStateView
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.ShowLinearProgressIndicator
import com.thomaskioko.tvmaniac.compose.components.SnackBarStyle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.components.TvManiacSnackBarHost
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.i18n.MR.strings.badge_new
import com.thomaskioko.tvmaniac.i18n.MR.strings.badge_premiere
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_filter
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_search
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_toggle_list_style
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_discover_up_next
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_up_to_date
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_watchlist_empty_result
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_item_my_shows
import com.thomaskioko.tvmaniac.i18n.MR.strings.msg_search_show_hint
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_not_watched_for_while
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.myshows.presenter.ChangeListStyleClicked
import com.thomaskioko.tvmaniac.myshows.presenter.ChangeMyShowsSortOption
import com.thomaskioko.tvmaniac.myshows.presenter.ClearMyShowsQuery
import com.thomaskioko.tvmaniac.myshows.presenter.MarkUpNextEpisodeWatched
import com.thomaskioko.tvmaniac.myshows.presenter.MyShowsAction
import com.thomaskioko.tvmaniac.myshows.presenter.MyShowsMessageShown
import com.thomaskioko.tvmaniac.myshows.presenter.MyShowsPresenter
import com.thomaskioko.tvmaniac.myshows.presenter.MyShowsQueryChanged
import com.thomaskioko.tvmaniac.myshows.presenter.MyShowsShowClicked
import com.thomaskioko.tvmaniac.myshows.presenter.MyShowsState
import com.thomaskioko.tvmaniac.myshows.presenter.RefreshMyShows
import com.thomaskioko.tvmaniac.myshows.presenter.ShowTitleClicked
import com.thomaskioko.tvmaniac.myshows.presenter.ToggleSearchActive
import com.thomaskioko.tvmaniac.myshows.presenter.UpNextEpisodeClicked
import com.thomaskioko.tvmaniac.myshows.presenter.model.MyShowsItem
import com.thomaskioko.tvmaniac.myshows.presenter.model.UpNextEpisodeItem
import com.thomaskioko.tvmaniac.myshows.ui.component.Searchbar
import com.thomaskioko.tvmaniac.testtags.myshows.MyShowsTestTags
import io.github.thomaskioko.codegen.annotations.TabUi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import java.util.Locale

@TabUi(presenter = MyShowsPresenter::class, parentScope = ActivityScope::class)
@Composable
public fun MyShowsScreen(
    presenter: MyShowsPresenter,
    modifier: Modifier = Modifier,
) {
    val libraryState by presenter.state.collectAsState()

    MyShowsScreen(
        modifier = modifier,
        state = libraryState,
        onAction = presenter::dispatch,
    )

    TvManiacSnackBarHost(
        message = libraryState.message?.message,
        style = SnackBarStyle.Error,
        onDismiss = { libraryState.message?.let { presenter.dispatch(MyShowsMessageShown(it.id)) } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MyShowsScreen(
    state: MyShowsState,
    modifier: Modifier = Modifier,
    onAction: (MyShowsAction) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var showSortOptions by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier
            .statusBarsPadding()
            .testTag(MyShowsTestTags.SCREEN_TEST_TAG),
        topBar = {
            TopBar(
                onAction = onAction,
                state = state,
                scrollBehavior = scrollBehavior,
                onSortClick = { showSortOptions = true },
            )
        },
        content = { contentPadding ->
            val context = LocalContext.current
            PullToRefreshBox(
                modifier = Modifier
                    .padding(contentPadding.copy(copyBottom = false))
                    .padding(horizontal = 8.dp),
                isRefreshing = state.isRefreshing,
                onRefresh = { onAction(RefreshMyShows(forceRefresh = true)) },
            ) {
                AnimatedContent(
                    targetState = state.isGridMode,
                    transitionSpec = {
                        (scaleIn(animationSpec = spring()) + fadeIn()) togetherWith
                            (scaleOut(animationSpec = spring()) + fadeOut())
                    },
                    label = "list_style_animation",
                ) { isGridMode ->
                    val hasNoItems = state.watchNextItems.isEmpty() && state.staleItems.isEmpty()
                    val hasNoEpisodes =
                        state.watchNextEpisodes.isEmpty() && state.staleEpisodes.isEmpty()

                    when {
                        state.showLoading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ) {
                                LoadingIndicator()
                            }
                        }

                        isGridMode -> {
                            if (hasNoItems) {
                                val message = if (state.query.isNotBlank()) {
                                    label_watchlist_empty_result.resolve(context).format(state.query)
                                } else {
                                    null
                                }
                                EmptyStateView(
                                    modifier = Modifier
                                        .testTag(MyShowsTestTags.EMPTY_STATE_TEST_TAG),
                                    imageVector = Icons.Outlined.Inbox,
                                    title = state.emptyStateText,
                                    message = message,
                                )
                            } else {
                                SectionedMyShowsGridContent(
                                    watchNextTitle = label_discover_up_next.resolve(context),
                                    staleTitle = title_not_watched_for_while.resolve(context),
                                    watchNextItems = state.watchNextItems,
                                    staleItems = state.staleItems,
                                    scrollBehavior = scrollBehavior,
                                    onItemClicked = { onAction(MyShowsShowClicked(it)) },
                                )
                            }
                        }

                        else -> {
                            if (hasNoEpisodes) {
                                EmptyStateView(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    title = label_up_to_date.resolve(context),
                                )
                            } else {
                                SectionedUpNextListContent(
                                    watchNextTitle = label_discover_up_next.resolve(context),
                                    staleTitle = title_not_watched_for_while.resolve(context),
                                    premiereLabel = badge_premiere.resolve(context),
                                    newLabel = badge_new.resolve(context),
                                    watchNextEpisodes = state.watchNextEpisodes,
                                    staleEpisodes = state.staleEpisodes,
                                    updatingEpisodeIds = state.updatingEpisodeIds,
                                    scrollBehavior = scrollBehavior,
                                    onEpisodeClicked = { showId, episodeId ->
                                        onAction(UpNextEpisodeClicked(showId, episodeId))
                                    },
                                    onShowTitleClicked = { showId ->
                                        onAction(ShowTitleClicked(showId))
                                    },
                                    onMarkWatched = { episode ->
                                        onAction(
                                            MarkUpNextEpisodeWatched(
                                                showTraktId = episode.showTraktId,
                                                episodeId = episode.episodeId,
                                                seasonNumber = episode.seasonNumber,
                                                episodeNumber = episode.episodeNumber,
                                            ),
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }
        },
    )

    if (showSortOptions) {
        ModalBottomSheet(
            onDismissRequest = { showSortOptions = false },
            sheetState = sheetState,
            modifier = Modifier.testTag(MyShowsTestTags.SORT_SHEET_TEST_TAG),
        ) {
            MyShowsSortOptionsContent(
                selectedSortOption = state.sortOption,
                onSortOptionSelected = { sortOption ->
                    onAction(ChangeMyShowsSortOption(sortOption))
                    scope.launch {
                        sheetState.hide()
                        showSortOptions = false
                    }
                },
            )
        }
    }
}

@Composable
private fun TopBar(
    onAction: (MyShowsAction) -> Unit,
    state: MyShowsState,
    scrollBehavior: TopAppBarScrollBehavior,
    onSortClick: () -> Unit,
) {
    val context = LocalContext.current

    BackHandler(enabled = state.isSearchActive) {
        onAction(ClearMyShowsQuery)
        onAction(ToggleSearchActive)
    }

    TvManiacTopBar(
        title = {
            AnimatedContent(
                targetState = state.isSearchActive,
                transitionSpec = {
                    (scaleIn(animationSpec = spring()) + fadeIn()) togetherWith
                        (scaleOut(animationSpec = spring()) + fadeOut())
                },
                label = "search_expansion_animation",
            ) { expanded ->
                if (expanded) {
                    Searchbar(
                        modifier = Modifier
                            .testTag(MyShowsTestTags.SEARCH_BAR_TEST_TAG),
                        query = state.query,
                        hint = msg_search_show_hint.resolve(context),
                        onQueryChanged = { onAction(MyShowsQueryChanged(it)) },
                        onCloseClick = {
                            onAction(ClearMyShowsQuery)
                            onAction(ToggleSearchActive)
                        },
                    )
                } else {
                    CollapsedTopBarContent(
                        state = state,
                        onAction = onAction,
                        onSearchClick = { onAction(ToggleSearchActive) },
                        onSortClick = onSortClick,
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
        ),
    )
}

@Composable
private fun CollapsedTopBarContent(
    state: MyShowsState,
    onAction: (MyShowsAction) -> Unit,
    onSearchClick: () -> Unit,
    onSortClick: () -> Unit,
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            modifier = Modifier
                .padding(end = 8.dp)
                .testTag(MyShowsTestTags.TOGGLE_LIST_STYLE_BUTTON_TEST_TAG),
            onClick = { onAction(ChangeListStyleClicked(state.isGridMode)) },
        ) {
            val image = if (state.isGridMode) {
                Icons.AutoMirrored.Outlined.List
            } else {
                Icons.Outlined.GridView
            }
            Icon(
                imageVector = image,
                contentDescription = cd_toggle_list_style.resolve(context),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }

        Text(
            text = menu_item_my_shows.resolve(context),
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Row(
            modifier = Modifier.padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            IconButton(
                modifier = Modifier.testTag(MyShowsTestTags.SEARCH_BUTTON_TEST_TAG),
                onClick = onSearchClick,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = cd_search.resolve(context),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }

            IconButton(
                modifier = Modifier.testTag(MyShowsTestTags.SORT_BUTTON_TEST_TAG),
                onClick = onSortClick,
            ) {
                Icon(
                    imageVector = Icons.Outlined.FilterList,
                    contentDescription = cd_filter.resolve(context),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }

            AnimatedVisibility(visible = state.isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    strokeWidth = 2.dp,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SectionedMyShowsGridContent(
    watchNextTitle: String,
    staleTitle: String,
    watchNextItems: ImmutableList<MyShowsItem>,
    staleItems: ImmutableList<MyShowsItem>,
    scrollBehavior: TopAppBarScrollBehavior,
    onItemClicked: (Long) -> Unit,
) {
    val chunkedWatchNext = remember(watchNextItems) {
        watchNextItems.chunked(3).map { it.toImmutableList() }.toImmutableList()
    }
    val chunkedStale = remember(staleItems) {
        staleItems.chunked(3).map { it.toImmutableList() }.toImmutableList()
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .testTag(MyShowsTestTags.MY_SHOWS_GRID_TEST_TAG)
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .padding(horizontal = 4.dp),
    ) {
        if (chunkedWatchNext.isNotEmpty()) {
            stickyHeader(key = "grid_header_watch_next") {
                SectionHeader(title = watchNextTitle)
            }
            items(
                items = chunkedWatchNext,
                key = { "watchnext_row_${it.first().traktId}" },
                contentType = { "WatchnextRow" },
            ) { rowItems ->
                GridRow(
                    items = rowItems,
                    onItemClicked = onItemClicked,
                )
            }
        }

        if (chunkedStale.isNotEmpty()) {
            stickyHeader(key = "grid_header_stale") {
                SectionHeader(title = staleTitle)
            }
            items(
                items = chunkedStale,
                key = { "stale_row_${it.first().traktId}" },
                contentType = { "StaleRow" },
            ) { rowItems ->
                GridRow(
                    items = rowItems,
                    onItemClicked = onItemClicked,
                )
            }
        }

        item {
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun GridRow(
    items: ImmutableList<MyShowsItem>,
    onItemClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items.forEach { show ->
            MyShowsGridItem(
                show = show,
                onItemClicked = onItemClicked,
                modifier = Modifier.weight(1f),
            )
        }
        repeat(3 - items.size) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun MyShowsGridItem(
    show: MyShowsItem,
    onItemClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter,
    ) {
        PosterCard(
            imageUrl = show.posterImageUrl,
            onClick = { onItemClicked(show.traktId) },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .testTag(MyShowsTestTags.showCard(show.traktId)),
            title = show.title.orEmpty(),
            shape = RectangleShape,
        )
        ShowLinearProgressIndicator(
            progress = show.watchProgress,
            modifier = Modifier
                .height(8.dp)
                .fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SectionedUpNextListContent(
    watchNextTitle: String,
    staleTitle: String,
    premiereLabel: String,
    newLabel: String,
    watchNextEpisodes: ImmutableList<UpNextEpisodeItem>,
    staleEpisodes: ImmutableList<UpNextEpisodeItem>,
    updatingEpisodeIds: ImmutableSet<Long>,
    scrollBehavior: TopAppBarScrollBehavior,
    onEpisodeClicked: (Long, Long) -> Unit,
    onShowTitleClicked: (Long) -> Unit,
    onMarkWatched: (UpNextEpisodeItem) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .testTag(MyShowsTestTags.MY_SHOWS_LIST_TEST_TAG)
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
        if (watchNextEpisodes.isNotEmpty()) {
            stickyHeader(key = "header_watch_next") {
                SectionHeader(
                    title = watchNextTitle,
                    modifier = Modifier.animateItem(),
                )
            }
            items(
                items = watchNextEpisodes,
                key = { "watchnext_${it.showTraktId}_${it.episodeId}" },
                contentType = { "WatchnextEpisode" },
            ) { episode ->
                MyShowsUpNextListItem(
                    item = episode,
                    premiereLabel = premiereLabel,
                    newLabel = newLabel,
                    onItemClicked = onEpisodeClicked,
                    onShowTitleClicked = { onShowTitleClicked(episode.showTraktId) },
                    onMarkWatched = { onMarkWatched(episode) },
                    modifier = Modifier.animateItem(),
                    isUpdating = episode.episodeId in updatingEpisodeIds,
                )
            }
        }

        if (staleEpisodes.isNotEmpty()) {
            stickyHeader(key = "header_stale") {
                SectionHeader(
                    title = staleTitle,
                    modifier = Modifier.animateItem(),
                )
            }
            items(
                items = staleEpisodes,
                key = { "stale_${it.showTraktId}_${it.episodeId}" },
                contentType = { "StaleEpisode" },
            ) { episode ->
                MyShowsUpNextListItem(
                    item = episode,
                    premiereLabel = premiereLabel,
                    newLabel = newLabel,
                    onItemClicked = onEpisodeClicked,
                    onShowTitleClicked = { onShowTitleClicked(episode.showTraktId) },
                    onMarkWatched = { onMarkWatched(episode) },
                    modifier = Modifier.animateItem(),
                    isUpdating = episode.episodeId in updatingEpisodeIds,
                )
            }
        }

        item {
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        ) {
            Text(
                text = title.uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun MyShowsScreenPreview(
    @PreviewParameter(MyShowsPreviewParameterProvider::class) state: MyShowsState,
) {
    MyShowsScreen(
        state = state,
        onAction = {},
    )
}
