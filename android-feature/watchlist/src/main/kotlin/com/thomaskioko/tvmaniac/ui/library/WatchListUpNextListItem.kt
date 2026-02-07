package com.thomaskioko.tvmaniac.ui.library

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.NewBadge
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.PremiereBadge
import com.thomaskioko.tvmaniac.compose.components.TextTitlePill
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.theme.grey
import com.thomaskioko.tvmaniac.watchlist.presenter.model.EpisodeBadge
import com.thomaskioko.tvmaniac.watchlist.presenter.model.UpNextEpisodeItem

@Composable
internal fun WatchListUpNextListItem(
    item: UpNextEpisodeItem,
    premiereLabel: String,
    newLabel: String,
    onItemClicked: (Long, Long) -> Unit,
    onShowTitleClicked: (Long) -> Unit,
    onMarkWatched: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = MaterialTheme.shapes.small,
        modifier = modifier.clickable { onItemClicked(item.showTraktId, item.episodeId) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.Top,
        ) {
            PosterCard(
                imageUrl = item.stillImage ?: item.showPoster,
                modifier = Modifier
                    .width(100.dp)
                    .aspectRatio(0.8f),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
            ) {
                TextTitlePill(
                    showName = item.showName,
                    onClick = { onShowTitleClicked(item.showTraktId) },
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = item.episodeNumberFormatted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    if (item.remainingEpisodes > 0) {
                        Text(
                            text = "+${item.remainingEpisodes}",
                            maxLines = 1,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        )
                    }
                }

                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = item.episodeTitle,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    when (item.badge) {
                        EpisodeBadge.PREMIERE -> PremiereBadge(text = premiereLabel)
                        EpisodeBadge.NEW -> NewBadge(text = newLabel)
                        null -> {}
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(12.dp)
                    .size(32.dp)
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
private fun WatchListUpNextListItemPreview() {
    TvManiacTheme {
        Surface {
            WatchListUpNextListItem(
                item = UpNextEpisodeItem(
                    showTraktId = 1L,
                    showName = "The Walking Dead: Daryl Dixon",
                    showPoster = "/poster.jpg",
                    episodeId = 123L,
                    episodeTitle = "L'âme Perdue",
                    episodeNumberFormatted = "S02 | E01",
                    seasonId = 1L,
                    seasonNumber = 2,
                    episodeNumber = 1,
                    runtime = "45 min",
                    stillImage = "/still.jpg",
                    overview = "Daryl washes ashore in France.",
                    remainingEpisodes = 7,
                ),
                premiereLabel = "PREMIERE",
                newLabel = "NEW",
                onItemClicked = { _, _ -> },
                onShowTitleClicked = {},
                onMarkWatched = {},
            )
        }
    }
}
