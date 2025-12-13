package com.thomaskioko.tvmaniac.ui.library

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.EmptyContent
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.SearchTextContainer
import com.thomaskioko.tvmaniac.compose.components.ShowLinearProgressIndicator
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_watchlist_empty_result
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_item_library
import com.thomaskioko.tvmaniac.i18n.MR.strings.msg_search_show_hint
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.watchlist.presenter.ChangeListStyleClicked
import com.thomaskioko.tvmaniac.watchlist.presenter.ClearWatchlistQuery
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistAction
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistPresenter
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistQueryChanged
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistShowClicked
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistState
import com.thomaskioko.tvmaniac.watchlist.presenter.model.WatchlistItem
import kotlinx.collections.immutable.ImmutableList

@Composable
fun WatchlistScreen(
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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val lazyListState = rememberLazyListState()

    Scaffold(
        modifier = modifier.statusBarsPadding(),
        topBar = {
            TopBar(
                onAction = onAction,
                state = state,
                scrollBehavior = scrollBehavior,
                listState = lazyListState,
            )
        },
        content = { contentPadding ->
            if (state.items.isEmpty()) {
                val message = if (state.query.isNotBlank()) {
                    label_watchlist_empty_result.resolve(LocalContext.current)
                        .format(state.query)
                } else {
                    null
                }

                EmptyContent(
                    imageVector = Icons.Outlined.Inbox,
                    message = message,
                )
            } else {
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
                    if (isGridMode) {
                        WatchlistGridContent(
                            list = state.items,
                            scrollBehavior = scrollBehavior,
                            onItemClicked = { onAction(WatchlistShowClicked(it)) },
                        )
                    } else {
                        WatchlistListContent(
                            list = state.items,
                            scrollBehavior = scrollBehavior,
                            onItemClicked = { onAction(WatchlistShowClicked(it)) },
                        )
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
    listState: LazyListState,
) {
    Column {
        TvManiacTopBar(
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = { onAction(ChangeListStyleClicked) },
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
                        text = menu_item_library.resolve(LocalContext.current),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    IconButton(
                        onClick = { /* TODO: Implement filter functionality */ },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
                            contentDescription = "Filter",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    AnimatedVisibility(visible = state.isLoading) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            },
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                scrolledContainerColor = MaterialTheme.colorScheme.background,
            ),
        )

        SearchTextContainer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp),
            query = state.query,
            hint = msg_search_show_hint.resolve(LocalContext.current),
            lazyListState = listState,
            content = {},
            onClearQuery = { onAction(ClearWatchlistQuery) },
            onQueryChanged = { onAction(WatchlistQueryChanged(it)) },
        )
    }
}

@Composable
private fun WatchlistGridContent(
    list: ImmutableList<WatchlistItem>,
    scrollBehavior: TopAppBarScrollBehavior,
    onItemClicked: (Long) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .padding(horizontal = 4.dp),
    ) {
        items(list) { show ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                shape = RectangleShape,
            ) {
                Box(
                    modifier = Modifier.animateItem(),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    PosterCard(
                        modifier = Modifier.fillMaxWidth(),
                        imageUrl = show.posterImageUrl,
                        title = show.title,
                        onClick = { onItemClicked(show.tmdbId) },
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
        }
    }
}

@Composable
private fun WatchlistListContent(
    list: ImmutableList<WatchlistItem>,
    scrollBehavior: TopAppBarScrollBehavior,
    onItemClicked: (Long) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
        items(list) { show ->
            WatchlistListItem(
                item = show,
                onItemClicked = onItemClicked,
                modifier = Modifier.animateItem(),
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
