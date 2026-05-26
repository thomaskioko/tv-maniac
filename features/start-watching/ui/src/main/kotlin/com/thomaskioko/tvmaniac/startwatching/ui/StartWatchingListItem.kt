package com.thomaskioko.tvmaniac.startwatching.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.TextTitlePill
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.grey
import com.thomaskioko.tvmaniac.startwatching.presenter.model.StartWatchingItem
import com.thomaskioko.tvmaniac.testtags.startwatching.StartWatchingTestTags

@Composable
internal fun StartWatchingListItem(
    item: StartWatchingItem,
    onClick: () -> Unit,
    onShowTitleClicked: () -> Unit,
    onMarkWatched: () -> Unit,
    modifier: Modifier = Modifier,
    isUpdating: Boolean = false,
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
            verticalAlignment = Alignment.Top,
        ) {
            PosterCard(
                imageUrl = item.stillImageUrl ?: item.posterImageUrl,
                modifier = Modifier
                    .width(100.dp)
                    .aspectRatio(0.8f),
            )

            if (item.episodeNumberFormatted != null) {
                EpisodeDetails(
                    item = item,
                    onShowTitleClicked = onShowTitleClicked,
                    modifier = Modifier.weight(1f),
                )
                MarkWatchedButton(
                    isUpdating = isUpdating,
                    onMarkWatched = onMarkWatched,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
            } else {
                ShowDetails(
                    item = item,
                    onShowTitleClicked = onShowTitleClicked,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun EpisodeDetails(
    item: StartWatchingItem,
    onShowTitleClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(vertical = 8.dp, horizontal = 16.dp),
    ) {
        TextTitlePill(
            showName = item.title,
            onClick = onShowTitleClicked,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = item.episodeNumberFormatted.orEmpty(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            item.runtime?.let { runtime ->
                Text(
                    text = runtime,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }
        }

        item.episodeTitle?.let { episodeTitle ->
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = episodeTitle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
private fun ShowDetails(
    item: StartWatchingItem,
    onShowTitleClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(vertical = 8.dp, horizontal = 16.dp),
    ) {
        TextTitlePill(
            showName = item.title,
            onClick = onShowTitleClicked,
        )
        item.year?.let { year ->
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = year,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun MarkWatchedButton(
    isUpdating: Boolean,
    onMarkWatched: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(12.dp)
            .size(32.dp)
            .background(color = grey, shape = CircleShape)
            .clickable(enabled = !isUpdating) { onMarkWatched() },
        contentAlignment = Alignment.Center,
    ) {
        if (isUpdating) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        } else {
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun StartWatchingListItemPreview() {
    StartWatchingListItem(
        item = StartWatchingItem(
            traktId = 1,
            title = "Breaking Bad",
            posterImageUrl = null,
            year = "2008",
            episodeId = 11,
            episodeTitle = "Pilot",
            episodeNumberFormatted = "S01 | E01",
            seasonNumber = 1,
            episodeNumber = 1,
            runtime = "58 min",
        ),
        onClick = {},
        onShowTitleClicked = {},
        onMarkWatched = {},
    )
}
