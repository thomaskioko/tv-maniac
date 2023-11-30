package com.thomaskioko.tvmaniac.feature.moreshows.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.SnackBarErrorRetry

@ExperimentalFoundationApi
@Composable
fun <T : Any> LazyPagedGridItems(
    listState: LazyGridState,
    lazyPagingItems: LazyPagingItems<T>,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    rows: Int = 3,
    hPadding: Int = 2,
    itemContent: @Composable LazyGridItemScope.(value: T?) -> Unit,
) {
    LazyVerticalGrid(
        modifier = modifier,
        state = listState,
        columns = GridCells.Fixed(rows),
    ) {
        items(lazyPagingItems.itemCount) { index ->
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(horizontal = hPadding.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1F)
                        .align(Alignment.Top)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    itemContent(lazyPagingItems[index])
                }
            }
        }

        lazyPagingItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item {
                        LoadingIndicator(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(Alignment.Center),
                        )
                    }
                }

                loadState.append is LoadState.Loading -> {
                    item {
                        LoadingIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally),
                        )
                    }
                }

                loadState.append is LoadState.Error -> {
                    val exception = lazyPagingItems.loadState.refresh as LoadState.Error
                    item {
                        SnackBarErrorRetry(
                            snackBarHostState = snackbarHostState,
                            errorMessage = exception.error.localizedMessage!!,
                            onErrorAction = { retry() },
                            actionLabel = "Retry",
                        )
                    }
                }

                loadState.refresh is LoadState.Error -> {
                    val exception = lazyPagingItems.loadState.append as LoadState.Error
                    item {
                        SnackBarErrorRetry(
                            snackBarHostState = snackbarHostState,
                            errorMessage = exception.error.localizedMessage!!,
                            onErrorAction = { retry() },
                            actionLabel = "Retry",
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalFoundationApi
fun <T : Any> LazyGridScope.items(
    lazyPagingItems: LazyPagingItems<T>,
    itemContent: @Composable LazyGridItemScope.(value: T?) -> Unit,
) {
    items(lazyPagingItems.itemCount) { index ->
        itemContent(lazyPagingItems[index])
    }
}
