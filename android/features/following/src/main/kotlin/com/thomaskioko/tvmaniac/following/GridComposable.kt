package com.thomaskioko.tvmaniac.following

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.util.copy

@Composable
fun <T> LazyGridItems(
    items: List<T> = listOf(),
    rows: Int = 3,
    hPadding: Int = 8,
    listState: LazyListState,
    paddingValues: PaddingValues,
    itemContent: @Composable LazyItemScope.(T) -> Unit
) {
    val chunkedList = items.chunked(rows)
    LazyColumn(
        state = listState,
        modifier = Modifier
            .padding(horizontal = hPadding.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = paddingValues.copy(copyTop = false),
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
