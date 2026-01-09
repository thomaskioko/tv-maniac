package com.thomaskioko.tvmaniac.showdetails.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.theme.green
import com.thomaskioko.tvmaniac.compose.theme.grey
import com.thomaskioko.tvmaniac.i18n.MR
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ContinueTrackingEpisodeModel

@Composable
internal fun ContinueTrackingCard(
    episode: ContinueTrackingEpisodeModel,
    onMarkWatched: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .width(300.dp)
            .height(120.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PosterCard(
                imageUrl = episode.imageUrl,
                imageWidth = 100.dp,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = episode.episodeNumberFormatted,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = episode.episodeTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            if (episode.hasAired) {
                Box(
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(28.dp)
                        .background(
                            color = if (episode.isWatched) green else grey,
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
            } else {
                val daysUntilAir = episode.daysUntilAir
                if (daysUntilAir != null && daysUntilAir > 0) {
                    Column(
                        modifier = Modifier.padding(end = 12.dp),
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
                } else {
                    Text(
                        text = "TBD",
                        modifier = Modifier.padding(end = 12.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun ContinueTrackingCardPreview() {
    TvManiacTheme {
        Surface {
            ContinueTrackingCard(
                episode = ContinueTrackingEpisodeModel(
                    episodeId = 123L,
                    seasonId = 1L,
                    showId = 1L,
                    episodeNumber = 3,
                    seasonNumber = 2,
                    episodeNumberFormatted = "S02 | E03",
                    episodeTitle = "Re:start",
                    imageUrl = "/still.jpg",
                    isWatched = false,
                    daysUntilAir = null,
                    hasAired = true,
                ),
                onMarkWatched = {},
            )
        }
    }
}

@ThemePreviews
@Composable
private fun ContinueTrackingCardWatchedPreview() {
    TvManiacTheme {
        Surface {
            ContinueTrackingCard(
                episode = ContinueTrackingEpisodeModel(
                    episodeId = 123L,
                    seasonId = 1L,
                    showId = 1L,
                    episodeNumber = 2,
                    seasonNumber = 2,
                    episodeNumberFormatted = "S02 | E02",
                    episodeTitle = "Previous Episode",
                    imageUrl = "/still.jpg",
                    isWatched = true,
                    daysUntilAir = null,
                    hasAired = true,
                ),
                onMarkWatched = {},
            )
        }
    }
}

@ThemePreviews
@Composable
private fun ContinueTrackingCardFuturePreview() {
    TvManiacTheme {
        Surface {
            ContinueTrackingCard(
                episode = ContinueTrackingEpisodeModel(
                    episodeId = 123L,
                    seasonId = 1L,
                    showId = 1L,
                    episodeNumber = 5,
                    seasonNumber = 2,
                    episodeNumberFormatted = "S02 | E05",
                    episodeTitle = "Upcoming Episode",
                    imageUrl = "/still.jpg",
                    isWatched = false,
                    daysUntilAir = 7,
                    hasAired = false
                ),
                onMarkWatched = {},
            )
        }
    }
}

@ThemePreviews
@Composable
private fun ContinueTrackingCardUnknownAirDatePreview() {
    TvManiacTheme {
        Surface {
            ContinueTrackingCard(
                episode = ContinueTrackingEpisodeModel(
                    episodeId = 123L,
                    seasonId = 1L,
                    showId = 1L,
                    episodeNumber = 6,
                    seasonNumber = 2,
                    episodeNumberFormatted = "S02 | E06",
                    episodeTitle = "Unknown Air Date",
                    imageUrl = "/still.jpg",
                    isWatched = false,
                    daysUntilAir = null,
                    hasAired = false
                ),
                onMarkWatched = {},
            )
        }
    }
}
