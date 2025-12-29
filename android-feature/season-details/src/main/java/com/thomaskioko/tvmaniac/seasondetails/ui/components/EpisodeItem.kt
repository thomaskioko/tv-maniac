package com.thomaskioko.tvmaniac.seasondetails.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.theme.green
import com.thomaskioko.tvmaniac.compose.theme.grey
import com.thomaskioko.tvmaniac.i18n.MR
import com.thomaskioko.tvmaniac.seasondetails.ui.episodeDetailsModel

@Composable
internal fun EpisodeItem(
    imageUrl: String?,
    title: String,
    episodeOverview: String,
    isWatched: Boolean,
    isProcessing: Boolean,
    onWatchedToggle: () -> Unit,
    modifier: Modifier = Modifier,
    daysUntilAir: Int? = null,
    shape: Shape = MaterialTheme.shapes.small,
    onEpisodeClicked: () -> Unit = {},
) {
    Card(
        shape = shape,
        modifier = modifier.clickable { onEpisodeClicked() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PosterCard(
                imageUrl = imageUrl,
                modifier = Modifier
                    .width(100.dp)
                    .aspectRatio(0.8f),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp, horizontal = 8.dp),
            ) {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 4.dp),
                )

                Text(
                    text = episodeOverview,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier,
                )
            }

            if (daysUntilAir != null && daysUntilAir > 0) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = daysUntilAir.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = pluralStringResource(
                            MR.plurals.day_label.resourceId,
                            daysUntilAir,
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(12.dp)
                        .size(28.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                )
            } else {
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .size(28.dp)
                        .background(
                            color = if (isWatched) green else grey,
                            shape = CircleShape,
                        )
                        .clickable { onWatchedToggle() },
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
}

@ThemePreviews
@Composable
private fun WatchlistRowItemPreview() {
    TvManiacTheme {
        Surface {
            EpisodeItem(
                title = episodeDetailsModel.episodeNumberTitle,
                episodeOverview = episodeDetailsModel.overview,
                imageUrl = episodeDetailsModel.imageUrl,
                isWatched = false,
                isProcessing = false,
                onWatchedToggle = {},
                onEpisodeClicked = {},
            )
        }
    }
}

@ThemePreviews
@Composable
private fun WatchlistRowItemWatchedPreview() {
    TvManiacTheme {
        Surface {
            EpisodeItem(
                title = episodeDetailsModel.episodeNumberTitle,
                episodeOverview = episodeDetailsModel.overview,
                imageUrl = episodeDetailsModel.imageUrl,
                isWatched = true,
                isProcessing = false,
                onWatchedToggle = {},
                onEpisodeClicked = {},
            )
        }
    }
}

@ThemePreviews
@Composable
private fun EpisodeItemFuturePreview() {
    TvManiacTheme {
        Surface {
            EpisodeItem(
                title = episodeDetailsModel.episodeNumberTitle,
                episodeOverview = episodeDetailsModel.overview,
                imageUrl = episodeDetailsModel.imageUrl,
                isWatched = false,
                isProcessing = false,
                daysUntilAir = 7,
                onWatchedToggle = {},
                onEpisodeClicked = {},
            )
        }
    }
}
