package com.thomaskioko.tvmaniac.startwatching.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.EmptyStateView
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_start_watching_empty
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.startwatching.presenter.StartWatchingAction
import com.thomaskioko.tvmaniac.startwatching.presenter.StartWatchingShowClicked
import com.thomaskioko.tvmaniac.startwatching.presenter.StartWatchingState
import com.thomaskioko.tvmaniac.startwatching.presenter.model.StartWatchingItem
import com.thomaskioko.tvmaniac.testtags.startwatching.StartWatchingTestTags
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
public fun StartWatchingScreen(
    state: StartWatchingState,
    modifier: Modifier = Modifier,
    onAction: (StartWatchingAction) -> Unit,
) {
    val onShowClicked: (Long) -> Unit = { onAction(StartWatchingShowClicked(it)) }

    when {
        state.showLoading -> LoadingIndicator(
            modifier = modifier
                .fillMaxSize()
                .testTag(StartWatchingTestTags.PROGRESS_INDICATOR),
        )

        state.isEmpty -> EmptyStateView(
            title = label_start_watching_empty.resolve(LocalContext.current),
            modifier = modifier
                .fillMaxSize()
                .testTag(StartWatchingTestTags.EMPTY_STATE),
        )

        state.isGridMode -> StartWatchingGrid(
            items = state.items,
            modifier = modifier,
            onShowClicked = onShowClicked,
        )

        else -> StartWatchingList(
            items = state.items,
            modifier = modifier,
            onShowClicked = onShowClicked,
        )
    }
}

@Composable
private fun StartWatchingGrid(
    items: ImmutableList<StartWatchingItem>,
    modifier: Modifier = Modifier,
    onShowClicked: (Long) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag(StartWatchingTestTags.GRID)
            .padding(horizontal = 4.dp),
    ) {
        items(
            items = items,
            key = { it.traktId },
        ) { item ->
            PosterCard(
                imageUrl = item.posterImageUrl,
                title = item.title,
                onClick = { onShowClicked(item.traktId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(StartWatchingTestTags.showCard(item.traktId)),
            )
        }
    }
}

@Composable
private fun StartWatchingList(
    items: ImmutableList<StartWatchingItem>,
    modifier: Modifier = Modifier,
    onShowClicked: (Long) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag(StartWatchingTestTags.LIST)
            .padding(horizontal = 8.dp, vertical = 8.dp),
    ) {
        items(
            count = items.size,
            key = { items[it].traktId },
        ) { index ->
            StartWatchingListRow(
                item = items[index],
                onClick = { onShowClicked(items[index].traktId) },
            )
        }
    }
}

@Composable
private fun StartWatchingListRow(
    item: StartWatchingItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = MaterialTheme.shapes.small,
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .testTag(StartWatchingTestTags.showCard(item.traktId)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
        ) {
            PosterCard(
                imageUrl = item.posterImageUrl,
                title = item.title,
                modifier = Modifier
                    .width(100.dp)
                    .aspectRatio(0.8f),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                item.year?.let { year ->
                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = year,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

internal val previewStartWatchingItems: ImmutableList<StartWatchingItem> = persistentListOf(
    StartWatchingItem(traktId = 1, title = "Breaking Bad", posterImageUrl = null, year = "2008"),
    StartWatchingItem(traktId = 2, title = "Better Call Saul", posterImageUrl = null, year = "2015"),
    StartWatchingItem(traktId = 3, title = "Severance", posterImageUrl = null, year = "2022"),
)

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun StartWatchingScreenContentPreview() {
    StartWatchingScreen(
        state = StartWatchingState(items = previewStartWatchingItems),
        onAction = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun StartWatchingScreenEmptyPreview() {
    StartWatchingScreen(
        state = StartWatchingState(),
        onAction = {},
    )
}
