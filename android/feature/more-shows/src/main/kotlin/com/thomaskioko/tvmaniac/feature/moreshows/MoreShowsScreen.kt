package com.thomaskioko.tvmaniac.feature.moreshows

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import app.cash.paging.LoadStateError
import app.cash.paging.LoadStateLoading
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.components.TvPosterCard
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.presentation.moreshows.MoreBackClicked
import com.thomaskioko.tvmaniac.presentation.moreshows.MoreShowsActions
import com.thomaskioko.tvmaniac.presentation.moreshows.MoreShowsPresenter
import com.thomaskioko.tvmaniac.presentation.moreshows.MoreShowsState
import com.thomaskioko.tvmaniac.presentation.moreshows.ShowClicked
import com.thomaskioko.tvmaniac.presentation.moreshows.TvShow

@Composable
fun MoreShowsScreen(
    presenter: MoreShowsPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.subscribeAsState()

    MoreShowsScreen(
        modifier = modifier,
        state = state,
        onAction = presenter::dispatch,
    )
}

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
internal fun MoreShowsScreen(
    state: MoreShowsState,
    onAction: (MoreShowsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagedList = state.list.collectAsLazyPagingItems()
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier
            .statusBarsPadding(),
        topBar = {
            TvManiacTopBar(
                showNavigationIcon = true,
                title = state.categoryTitle,
                onBackClick = { onAction(MoreBackClicked) },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
    ) { contentPadding ->

        GridContent(
            contentPadding = contentPadding,
            lazyPagingItems = pagedList,
            snackBarHostState = snackBarHostState,
            onItemClicked = { onAction(ShowClicked(it)) },
        )
    }
}

@ExperimentalFoundationApi
@Composable
fun GridContent(
    lazyPagingItems: LazyPagingItems<TvShow>,
    snackBarHostState: SnackbarHostState,
    contentPadding: PaddingValues,
    onItemClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyGridState()

    LaunchedEffect(Unit) {
        if (lazyPagingItems.loadState.append is LoadStateError) {
            val errorMessage = (lazyPagingItems.loadState.append as LoadStateError).error.message

            val displayMessage = "Failed to fetch data: $errorMessage"
            snackBarHostState.showSnackbar(displayMessage)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        if (lazyPagingItems.loadState.refresh == LoadStateLoading) {
            LoadingIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            state = listState,
            modifier = modifier
                .padding(horizontal = 4.dp)
                .padding(contentPadding),
        ) {
            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { it.tmdbId },
                contentType = { lazyPagingItems[it] },
            ) { index ->

                val show = lazyPagingItems[index]
                show?.let {
                    TvPosterCard(
                        modifier = Modifier
                            .animateItemPlacement(),
                        posterImageUrl = show.posterImageUrl,
                        title = show.title,
                        onClick = { onItemClicked(show.tmdbId) },
                    )
                }
            }
        }

        if (lazyPagingItems.loadState.append == LoadStateLoading) {
            LoadingIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
                    .padding(24.dp),
            )
        }
    }
}

@ThemePreviews
@Composable
private fun ShowsGridContentPreview(
    @PreviewParameter(MoreShowsPreviewParameterProvider::class)
    state: MoreShowsState,
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
