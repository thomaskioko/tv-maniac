package com.thomaskioko.tvmaniac.moreshows.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreBackClicked
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowClicked
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsActions
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsPresenter
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsState
import com.thomaskioko.tvmaniac.moreshows.presentation.RefreshMoreShows
import com.thomaskioko.tvmaniac.moreshows.presentation.TvShow

@Composable
public fun MoreShowsScreen(
    presenter: MoreShowsPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsState()

    MoreShowsScreen(
        modifier = modifier,
        state = state,
        onAction = presenter::dispatch,
    )
}

@Composable
internal fun MoreShowsScreen(
    state: MoreShowsState,
    onAction: (MoreShowsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val pagedList = state.pagingDataFlow.collectAsLazyPagingItems()
    val snackBarHostState = remember { SnackbarHostState() }
    val refreshing = remember { pagedList.loadState.refresh is LoadState.Loading }

    val refreshState = rememberPullRefreshState(
        refreshing = false,
        onRefresh = { onAction(RefreshMoreShows) },
    )

    Scaffold(
        modifier = modifier.statusBarsPadding(),
        topBar = {
            TvManiacTopBar(
                title = {
                    Text(
                        text = state.categoryTitle ?: "",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                    )
                },
                navigationIcon = {
                    Image(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier
                            .clickable(onClick = { onAction(MoreBackClicked) })
                            .padding(16.dp),
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
    ) { contentPadding ->
        GridContent(
            contentPadding = contentPadding,
            lazyPagingItems = pagedList,
            scrollBehavior = scrollBehavior,
            refreshState = refreshState,
            snackBarHostState = snackBarHostState,
            refreshing = refreshing,
            onAction = onAction,
        )
    }
}

@ExperimentalFoundationApi
@Composable
internal fun GridContent(
    lazyPagingItems: LazyPagingItems<TvShow>,
    scrollBehavior: TopAppBarScrollBehavior,
    snackBarHostState: SnackbarHostState,
    contentPadding: PaddingValues,
    refreshing: Boolean,
    refreshState: PullRefreshState,
    onAction: (MoreShowsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyGridState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(state = refreshState),
        contentAlignment = Alignment.Center,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            state = listState,
            modifier = modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(contentPadding)
                .padding(horizontal = 4.dp)
                .fillMaxHeight(),
        ) {
            items(
                count = lazyPagingItems.itemCount,
                key = { index -> index },
                contentType = { lazyPagingItems[it] },
            ) { index ->
                val show = lazyPagingItems[index]
                show?.let {
                    PosterCard(
                        modifier = Modifier
                            .animateItem()
                            .fillMaxWidth(),
                        imageUrl = show.posterImageUrl,
                        title = show.title,
                        onClick = { onAction(MoreShowClicked(show.tmdbId)) },
                    )
                }
            }

            if (lazyPagingItems.loadState.append == LoadState.Loading) {
                item(span = { GridItemSpan(1) }) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                    ) {
                        LoadingIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(Alignment.Center)
                                .padding(24.dp),
                        )
                    }
                }
            }
        }

        PullRefreshIndicator(
            refreshing = refreshing,
            state = refreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(contentPadding),
            scale = true,
            backgroundColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.secondary,
        )
    }

    HandleListLoadState(
        loadState = lazyPagingItems.loadState,
        snackBarHostState = snackBarHostState,
    )
}

@Composable
private fun HandleListLoadState(
    loadState: CombinedLoadStates,
    snackBarHostState: SnackbarHostState,
) {
    when (loadState.append) {
        is LoadState.Error -> {
            val errorMessage = (loadState.append as LoadState.Error).error.message
            errorMessage?.let { ShowSnackBarError(errorMessage, snackBarHostState) }
        }
        else -> {
            // No-op
        }
    }

    when (loadState.prepend) {
        is LoadState.Error -> {
            val errorMessage = (loadState.prepend as LoadState.Error).error.message
            errorMessage?.let { ShowSnackBarError(errorMessage, snackBarHostState) }
        }
        else -> {
            // No-op
        }
    }
}

@Composable
private fun ShowSnackBarError(
    errorMessage: String,
    snackBarHostState: SnackbarHostState,
) {
    LaunchedEffect(Unit) { snackBarHostState.showSnackbar(errorMessage) }
}

@ThemePreviews
@Composable
private fun ShowsGridContentPreview(
    @PreviewParameter(MoreShowsPreviewParameterProvider::class) state: MoreShowsState,
) {
    TvManiacTheme {
        Surface {
            MoreShowsScreen(
                state = state,
                onAction = {},
            )
        }
    }
}
