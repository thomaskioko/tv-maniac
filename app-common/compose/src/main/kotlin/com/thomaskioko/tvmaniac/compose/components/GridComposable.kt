package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyGridScope
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.CoroutineScope

@Composable
fun <T> LazyGridItems(
    items: List<T> = listOf(),
    rows: Int = 3,
    hPadding: Int = 8,
    listState: LazyListState,
    itemContent: @Composable LazyItemScope.(T) -> Unit
) {
    val chunkedList = items.chunked(rows)
    LazyColumn(
        state = listState,
        modifier = Modifier
            .padding(horizontal = hPadding.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {

        itemsIndexed(chunkedList) { _, item ->
            Row(modifier = Modifier) {
                item.forEachIndexed { _, item ->
                    Box(
                        modifier = Modifier
                            .weight(1F)
                            .align(Alignment.Top)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        itemContent(item)
                    }
                }
                repeat(rows - item.size) {
                    Box(
                        modifier = Modifier
                            .weight(1F)
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun <T : Any> LazyPagedGridItems(
    listState: LazyListState,
    lazyPagingItems: LazyPagingItems<T>,
    hostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    rows: Int = 3,
    hPadding: Int = 2,
    itemContent: @Composable LazyItemScope.(value: T?) -> Unit
) {
    LazyVerticalGrid(
        state = listState,
        cells = GridCells.Fixed(rows),
    ) {

        items(lazyPagingItems.itemCount) { index ->
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(horizontal = hPadding.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1F)
                        .align(Alignment.Top)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    itemContent(lazyPagingItems[index])
                }
            }
        }

        lazyPagingItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item { CircularLoadingView() }
                }
                loadState.append is LoadState.Loading -> {
                    item { LoadingItem() }
                }
                loadState.append is LoadState.Error -> {
                    val exception = lazyPagingItems.loadState.refresh as LoadState.Error
                    item {
                        SnackBarErrorRetry(
                            snackBarHostState = hostState,
                            coroutineScope = coroutineScope,
                            errorMessage = exception.error.localizedMessage!!,
                            onErrorAction = { retry() }
                        )
                    }
                }
                loadState.refresh is LoadState.Error -> {
                    val exception = lazyPagingItems.loadState.append as LoadState.Error
                    item {
                        SnackBarErrorRetry(
                            snackBarHostState = hostState,
                            coroutineScope = coroutineScope,
                            errorMessage = exception.error.localizedMessage!!,
                            onErrorAction = { retry() }
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
    itemContent: @Composable LazyItemScope.(value: T?) -> Unit
) {
    items(lazyPagingItems.itemCount) { index ->
        itemContent(lazyPagingItems[index])
    }
}
