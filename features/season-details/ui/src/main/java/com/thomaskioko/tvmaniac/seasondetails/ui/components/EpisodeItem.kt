package com.thomaskioko.tvmaniac.seasondetails.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.MarkWatchedButton
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.i18n.MR
import com.thomaskioko.tvmaniac.seasondetails.ui.episodeDetailsModel
import com.thomaskioko.tvmaniac.testtags.seasondetails.SeasonDetailsTestTags

@Composable
internal fun EpisodeItem(
    imageUrl: String?,
    title: String,
    episodeOverview: String,
    isWatched: Boolean,
    isProcessing: Boolean,
    hasAired: Boolean,
    onWatchedToggle: () -> Unit,
    modifier: Modifier = Modifier,
    episodeId: Long? = null,
    daysUntilAir: Int? = null,
    shape: Shape = MaterialTheme.shapes.small,
    onEpisodeClicked: () -> Unit = {},
) {
    Card(
        shape = shape,
        onClick = onEpisodeClicked,
        modifier = modifier,
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

            if (hasAired) {
                val toggleTag = episodeId?.let {
                    if (isWatched) {
                        SeasonDetailsTestTags.markEpisodeUnwatchedButton(it)
                    } else {
                        SeasonDetailsTestTags.markEpisodeWatchedButton(it)
                    }
                }

                MarkWatchedButton(
                    isWatched = isWatched,
                    isUpdating = isProcessing,
                    onToggle = onWatchedToggle,
                    modifier = Modifier
                        .padding(12.dp)
                        .let { if (toggleTag != null) it.testTag(toggleTag) else it },
                )
            } else if (daysUntilAir != null && daysUntilAir > 0) {
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
            } else {
                Text(
                    text = "TBD",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun WatchlistRowItemPreview() {
    EpisodeItem(
        title = episodeDetailsModel.episodeNumberTitle,
        episodeOverview = episodeDetailsModel.overview,
        imageUrl = episodeDetailsModel.imageUrl,
        isWatched = false,
        isProcessing = false,
        hasAired = true,
        onWatchedToggle = {},
        onEpisodeClicked = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun WatchlistRowItemWatchedPreview() {
    EpisodeItem(
        title = episodeDetailsModel.episodeNumberTitle,
        episodeOverview = episodeDetailsModel.overview,
        imageUrl = episodeDetailsModel.imageUrl,
        isWatched = true,
        isProcessing = false,
        hasAired = true,
        onWatchedToggle = {},
        onEpisodeClicked = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun EpisodeItemFuturePreview() {
    EpisodeItem(
        title = episodeDetailsModel.episodeNumberTitle,
        episodeOverview = episodeDetailsModel.overview,
        imageUrl = episodeDetailsModel.imageUrl,
        isWatched = false,
        isProcessing = false,
        hasAired = false,
        daysUntilAir = 7,
        onWatchedToggle = {},
        onEpisodeClicked = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun EpisodeItemUnknownAirDatePreview() {
    EpisodeItem(
        title = episodeDetailsModel.episodeNumberTitle,
        episodeOverview = episodeDetailsModel.overview,
        imageUrl = episodeDetailsModel.imageUrl,
        isWatched = false,
        isProcessing = false,
        hasAired = false,
        onWatchedToggle = {},
        onEpisodeClicked = {},
    )
}
