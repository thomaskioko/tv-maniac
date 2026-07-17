package com.thomaskioko.tvmaniac.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.Layout
import com.thomaskioko.tvmaniac.compose.theme.TvManiacElevation
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.i18n.MR.plurals.episode_count
import com.thomaskioko.tvmaniac.i18n.MR.plurals.season_count
import com.thomaskioko.tvmaniac.presentation.library.model.LibraryShowItem
import com.thomaskioko.tvmaniac.ui.library.preview.LibraryListItemPreviewParameterProvider
import java.text.DecimalFormat

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun LibraryListItem(
    item: LibraryShowItem,
    onItemClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = TvManiacElevation.medium,
        onClick = { onItemClicked(item.showId) },
    ) {
        Row {
            PosterCard(
                imageUrl = item.posterImageUrl,
                title = item.title,
                imageWidth = Layout.posterWidthFixed,
                aspectRatio = 120f / 200f,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(
                        start = TvManiacSpacing.medium,
                        end = TvManiacSpacing.small,
                        top = TvManiacSpacing.small,
                        bottom = TvManiacSpacing.small,
                    ),
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(TvManiacSpacing.xxSmall))

                item.rating?.let { rating ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.xxSmall),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = DecimalFormat("#.#").format(rating),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(TvManiacSpacing.xxSmall))

                val metadata = buildMetadataString(item)
                if (metadata.isNotEmpty()) {
                    Text(
                        text = metadata,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (item.watchProviders.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.xxSmall),
                        verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.xxSmall),
                    ) {
                        item.watchProviders.take(6).forEach { provider ->
                            AsyncImageComposable(
                                model = provider.logoUrl,
                                contentDescription = provider.name,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(MaterialTheme.shapes.small),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun buildMetadataString(item: LibraryShowItem): String = buildString {
    item.year?.let { append(it) }
    item.status?.let {
        if (isNotEmpty()) append(" · ")
        append(it)
    }
    if (item.seasonCount > 0) {
        val seasonNumber = item.seasonCount.toInt()
        if (isNotEmpty()) append(" · ")
        append(pluralStringResource(season_count.resourceId, seasonNumber, seasonNumber))
    }
    if (item.episodeCount > 0) {
        val episodeNumber = item.episodeCount.toInt()
        if (isNotEmpty()) append(" · ")
        append(pluralStringResource(episode_count.resourceId, episodeNumber, episodeNumber))
    }
    item.genres?.firstOrNull()?.let { genre ->
        if (isNotEmpty()) append(" · ")
        append(genre)
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun LibraryListItemPreview(
    @PreviewParameter(LibraryListItemPreviewParameterProvider::class) item: LibraryShowItem,
) {
    LibraryListItem(
        item = item,
        onItemClicked = {},
    )
}
