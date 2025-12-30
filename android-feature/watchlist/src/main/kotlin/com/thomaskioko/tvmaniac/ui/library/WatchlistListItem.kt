package com.thomaskioko.tvmaniac.ui.library

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.theme.green
import com.thomaskioko.tvmaniac.i18n.MR.plurals.episode_count
import com.thomaskioko.tvmaniac.i18n.MR.plurals.season_count
import com.thomaskioko.tvmaniac.watchlist.presenter.model.WatchlistItem

@Composable
internal fun WatchlistListItem(
    item: WatchlistItem,
    onItemClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp),
        onClick = { onItemClicked(item.tmdbId) },
        shape = RectangleShape,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            // Poster image
            PosterCard(
                imageWidth = 100.dp,
                imageUrl = item.posterImageUrl,
                title = item.title,
                onClick = { onItemClicked(item.tmdbId) },
                shape = RectangleShape,
            )

            Column(
                modifier = Modifier.fillMaxHeight(),
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = 8.dp,
                    ),
                )

                Spacer(modifier = Modifier.height(4.dp))

                val divider = buildAnnotatedString {
                    val tagStyle = MaterialTheme.typography.labelMedium
                        .toSpanStyle()
                        .copy(
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    withStyle(tagStyle) { append("  •  ") }
                }

                val text = buildAnnotatedString {
                    val statusStyle = MaterialTheme.typography.labelMedium
                        .toSpanStyle()
                        .copy(
                            color = MaterialTheme.colorScheme.secondary,
                            background = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
                        )

                    val tagStyle = MaterialTheme.typography.labelMedium
                        .toSpanStyle()
                        .copy(
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                    AnimatedVisibility(visible = !item.status.isNullOrBlank()) {
                        item.status?.let {
                            withStyle(statusStyle) {
                                append(" ")
                                append(it)
                                append(" ")
                            }
                            append(divider)
                        }
                    }

                    withStyle(tagStyle) { append(item.year) }
                }

                val resources = LocalContext.current.resources

                val seasonDetails = buildString {
                    if (item.seasonCount > 0) {
                        append(
                            resources.getQuantityString(
                                season_count.resourceId,
                                item.seasonCount.toInt(),
                                item.seasonCount.toInt(),
                            ),
                        )
                    }
                    if (item.seasonCount > 0 && item.episodeCount > 0) {
                        append(" • ")
                    }
                    if (item.episodeCount > 0) {
                        append(
                            resources.getQuantityString(
                                episode_count.resourceId,
                                item.episodeCount.toInt(),
                                item.episodeCount.toInt(),
                            ),
                        )
                    }
                }

                Text(
                    text = seasonDetails,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp), // Adjusted padding
                )

                Spacer(modifier = Modifier.height(4.dp)) // Spacer

                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp), // Adjusted padding
                )

                Spacer(modifier = Modifier.weight(1f))

                LinearProgressIndicator(
                    progress = { item.watchProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = if (item.watchProgress == 1f) {
                        green.copy(alpha = 0.5F)
                    } else {
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.5F)
                    },
                    strokeCap = StrokeCap.Butt,
                    drawStopIndicator = {},
                    gapSize = 0.dp,
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun WatchlistListItemPreview() {
    TvManiacTheme {
        Surface {
            WatchlistListItem(
                item = watchlistItems[0],
                onItemClicked = {},
            )
        }
    }
}
