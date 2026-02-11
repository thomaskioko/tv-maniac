package com.thomaskioko.tvmaniac.ui.library

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.EmptyContent
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.ShowLinearProgressIndicator
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.i18n.MR.strings.badge_new
import com.thomaskioko.tvmaniac.i18n.MR.strings.badge_premiere
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_search
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_discover_up_next
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_up_to_date
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_watchlist_empty_result
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_item_library
import com.thomaskioko.tvmaniac.i18n.MR.strings.msg_search_show_hint
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_not_watched_for_while
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.ui.library.component.Searchbar
import com.thomaskioko.tvmaniac.watchlist.presenter.ChangeListStyleClicked
import com.thomaskioko.tvmaniac.watchlist.presenter.ClearWatchlistQuery
import com.thomaskioko.tvmaniac.watchlist.presenter.MarkUpNextEpisodeWatched
import com.thomaskioko.tvmaniac.watchlist.presenter.ShowTitleClicked
import com.thomaskioko.tvmaniac.watchlist.presenter.ToggleSearchActive
import com.thomaskioko.tvmaniac.watchlist.presenter.UpNextEpisodeClicked
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistAction
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistPresenter
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistQueryChanged
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistShowClicked
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistState
import com.thomaskioko.tvmaniac.watchlist.presenter.model.UpNextEpisodeItem
import com.thomaskioko.tvmaniac.watchlist.presenter.model.WatchlistItem
import kotlinx.collections.immutable.ImmutableList
import java.util.Locale

@Composable
public fun WatchlistScreen(
    presenter: WatchlistPresenter,
    modifier: Modifier = Modifier,
) {
    val libraryState by presenter.state.collectAsState()

    WatchlistScreen(
        modifier = modifier,
        state = libraryState,
        onAction = presenter::dispatch,
    )
}

@Composable
internal fun WatchlistScreen(
    state: WatchlistState,
    modifier: Modifier = Modifier,
    onAction: (WatchlistAction) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier
            .statusBarsPadding(),
        topBar = {
            TopBar(
                onAction = onAction,
                state = state,
                scrollBehavior = scrollBehavior,
            )
        },
        content = { contentPadding ->
            val context = LocalContext.current
            AnimatedContent(
                modifier = Modifier
                    .padding(contentPadding.copy(copyBottom = false))
                    .padding(horizontal = 8.dp),
                targetState = state.isGridMode,
                transitionSpec = {
                    (scaleIn(animationSpec = spring()) + fadeIn()) togetherWith
                        (scaleOut(animationSpec = spring()) + fadeOut())
                },
                label = "list_style_animation",
            ) { isGridMode ->
                val hasNoItems = state.watchNextItems.isEmpty() && state.staleItems.isEmpty()
                val hasNoEpisodes = state.watchNextEpisodes.isEmpty() && state.staleEpisodes.isEmpty()

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
                            EmptyContent(
                                imageVector = Icons.Outlined.Inbox,
                                message = message,
                            )
                        } else {
                            SectionedWatchlistGridContent(
                                watchNextTitle = label_discover_up_next.resolve(context),
                                staleTitle = title_not_watched_for_while.resolve(context),
                                watchNextItems = state.watchNextItems,
                                staleItems = state.staleItems,
                                scrollBehavior = scrollBehavior,
                                onItemClicked = { onAction(WatchlistShowClicked(it)) },
                            )
                        }
                    }
                    else -> {
                        if (hasNoEpisodes) {
                            EmptyContent(
                                imageVector = Icons.Outlined.CheckCircle,
                                message = label_up_to_date.resolve(context),
                            )
                        } else {
                            SectionedUpNextListContent(
                                watchNextTitle = label_discover_up_next.resolve(context),
                                staleTitle = title_not_watched_for_while.resolve(context),
                                premiereLabel = badge_premiere.resolve(context),
                                newLabel = badge_new.resolve(context),
                                watchNextEpisodes = state.watchNextEpisodes,
                                staleEpisodes = state.staleEpisodes,
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
        },
    )
}

@Composable
private fun TopBar(
    onAction: (WatchlistAction) -> Unit,
    state: WatchlistState,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val context = LocalContext.current

    BackHandler(enabled = state.isSearchActive) {
        onAction(ClearWatchlistQuery)
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
                        query = state.query,
                        hint = msg_search_show_hint.resolve(context),
                        onQueryChanged = { onAction(WatchlistQueryChanged(it)) },
                        onCloseClick = {
                            onAction(ClearWatchlistQuery)
                            onAction(ToggleSearchActive)
                        },
                    )
                } else {
                    CollapsedTopBarContent(
                        state = state,
                        onAction = onAction,
                        onSearchClick = { onAction(ToggleSearchActive) },
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
    state: WatchlistState,
    onAction: (WatchlistAction) -> Unit,
    onSearchClick: () -> Unit,
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            modifier = Modifier.padding(end = 8.dp),
            onClick = { onAction(ChangeListStyleClicked(state.isGridMode)) },
        ) {
            val image = if (state.isGridMode) {
                Icons.AutoMirrored.Outlined.List
            } else {
                Icons.Outlined.GridView
            }
            Icon(
                imageVector = image,
                contentDescription = "Toggle list style",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }

        Text(
            text = menu_item_library.resolve(context),
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
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = cd_search.resolve(context),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }

            IconButton(
                onClick = { /* TODO: Implement filter functionality */ },
            ) {
                Icon(
                    imageVector = Icons.Outlined.FilterList,
                    contentDescription = "Filter",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }

            AnimatedVisibility(visible = state.isRefreshing || state.isSyncing) {
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
private fun SectionedWatchlistGridContent(
    watchNextTitle: String,
    staleTitle: String,
    watchNextItems: ImmutableList<WatchlistItem>,
    staleItems: ImmutableList<WatchlistItem>,
    scrollBehavior: TopAppBarScrollBehavior,
    onItemClicked: (Long) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .padding(horizontal = 4.dp),
    ) {
        if (watchNextItems.isNotEmpty()) {
            stickyHeader(key = "grid_header_watch_next") {
                SectionHeader(title = watchNextTitle)
            }
            val chunkedWatchNext = watchNextItems.chunked(3)
            items(chunkedWatchNext.size, key = { "watchnext_row_$it" }) { rowIndex ->
                GridRow(
                    items = chunkedWatchNext[rowIndex],
                    onItemClicked = onItemClicked,
                )
            }
        }

        if (staleItems.isNotEmpty()) {
            stickyHeader(key = "grid_header_stale") {
                SectionHeader(title = staleTitle)
            }
            val chunkedStale = staleItems.chunked(3)
            items(chunkedStale.size, key = { "stale_row_$it" }) { rowIndex ->
                GridRow(
                    items = chunkedStale[rowIndex],
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
    items: List<WatchlistItem>,
    onItemClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items.forEach { show ->
            WatchlistGridItem(
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
private fun WatchlistGridItem(
    show: WatchlistItem,
    onItemClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter,
    ) {
        PosterCard(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f),
            imageUrl = show.posterImageUrl,
            title = show.title,
            onClick = { onItemClicked(show.traktId) },
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
    scrollBehavior: TopAppBarScrollBehavior,
    onEpisodeClicked: (Long, Long) -> Unit,
    onShowTitleClicked: (Long) -> Unit,
    onMarkWatched: (UpNextEpisodeItem) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
        if (watchNextEpisodes.isNotEmpty()) {
            stickyHeader(key = "header_watch_next") {
                SectionHeader(
                    title = watchNextTitle,
                    modifier = Modifier.animateItem(),
                )
            }
            items(watchNextEpisodes, key = { "watchnext_${it.showTraktId}_${it.episodeId}" }) { episode ->
                WatchListUpNextListItem(
                    item = episode,
                    premiereLabel = premiereLabel,
                    newLabel = newLabel,
                    onItemClicked = onEpisodeClicked,
                    onShowTitleClicked = { onShowTitleClicked(episode.showTraktId) },
                    onMarkWatched = { onMarkWatched(episode) },
                    modifier = Modifier.animateItem(),
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
            items(staleEpisodes, key = { "stale_${it.showTraktId}_${it.episodeId}" }) { episode ->
                WatchListUpNextListItem(
                    item = episode,
                    premiereLabel = premiereLabel,
                    newLabel = newLabel,
                    onItemClicked = onEpisodeClicked,
                    onShowTitleClicked = { onShowTitleClicked(episode.showTraktId) },
                    onMarkWatched = { onMarkWatched(episode) },
                    modifier = Modifier.animateItem(),
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
@Composable
private fun WatchlistScreenPreview(
    @PreviewParameter(WatchlistPreviewParameterProvider::class) state: WatchlistState,
) {
    TvManiacTheme {
        Surface {
            WatchlistScreen(
                state = state,
                onAction = {},
            )
        }
    }
}
