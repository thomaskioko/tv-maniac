package com.thomaskioko.tvmaniac.ui.upnext

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.TextTitlePill
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.theme.grey
import com.thomaskioko.tvmaniac.presentation.upnext.model.UpNextEpisodeUiModel
import com.thomaskioko.tvmaniac.ui.upnext.preview.UpNextEpisodePreviewParameterProvider

@Composable
internal fun UpNextListItem(
    item: UpNextEpisodeUiModel,
    onItemClicked: (Long) -> Unit,
    onMarkWatched: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp),
        color = MaterialTheme.colorScheme.surface,
        onClick = { onItemClicked(item.showTraktId) },
    ) {
        Row(
            modifier = Modifier.clickable(onClick = { onItemClicked(item.showTraktId) }),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PosterCard(
                imageUrl = item.imageUrl,
                title = item.showName,
                imageWidth = 100.dp,
                aspectRatio = 100f / 140f,
                onClick = { onItemClicked(item.showTraktId) },
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(
                        start = 12.dp,
                        end = 4.dp,
                        top = 12.dp,
                        bottom = 12.dp,
                    ),
            ) {
                TextTitlePill(
                    showName = item.showName,
                    onClick = { onItemClicked(item.showTraktId) },
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = buildString {
                        append(item.formattedEpisodeNumber)
                        if (item.remainingEpisodes > 0) append(" +${item.remainingEpisodes}")
                        item.formattedRuntime?.let { append(" ($it)") }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                )

                item.episodeName?.let { name ->
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val progress = if (item.totalCount > 0) {
                        item.watchedCount.toFloat() / item.totalCount.toFloat()
                    } else {
                        0f
                    }

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                        strokeCap = StrokeCap.Round,
                    )

                    if (item.totalCount > 0) {
                        Text(
                            text = "${item.watchedCount}/${item.totalCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .size(28.dp)
                    .background(
                        color = grey,
                        shape = CircleShape,
                    )
                    .clickable { onMarkWatched() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun UpNextListItemPreview(
    @PreviewParameter(UpNextEpisodePreviewParameterProvider::class) item: UpNextEpisodeUiModel,
) {
    TvManiacTheme {
        UpNextListItem(
            item = item,
            onItemClicked = {},
            onMarkWatched = {},
        )
    }
}
