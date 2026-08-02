package com.thomaskioko.tvmaniac.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.PosterPlaceholder
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.ImageType
import com.thomaskioko.tvmaniac.compose.theme.Layout
import com.thomaskioko.tvmaniac.compose.theme.TvManiacElevation
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_show_poster
import com.thomaskioko.tvmaniac.presentation.library.model.LibraryShowItem
import com.thomaskioko.tvmaniac.ui.library.preview.LibraryListItemPreviewParameterProvider

private val BackdropAspect = 16f / 9f
private val CompactPlaceholderIconSize = 24.dp
private val DetailedScrimHeight = 96.dp

@Composable
internal fun LibraryCompactItem(
    item: LibraryShowItem,
    onItemClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = TvManiacSpacing.small),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = TvManiacElevation.medium,
        onClick = { onItemClicked(item.showId) },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // The artwork sits flush against the card edge and sets the row height.
            PosterCard(
                imageUrl = item.posterImageUrl,
                title = item.title,
                imageWidth = Layout.compactThumbnailWidth,
                aspectRatio = ImageType.Poster.aspect,
                placeholderIconSize = CompactPlaceholderIconSize,
                shape = RectangleShape,
            )

            Column(
                modifier = Modifier.padding(TvManiacSpacing.small),
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                val metadata = buildMetadataString(item)
                if (metadata.isNotEmpty()) {
                    Text(
                        text = metadata,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
internal fun LibraryDetailedItem(
    item: LibraryShowItem,
    onItemClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val surface = MaterialTheme.colorScheme.surface
    val scrim = remember(surface) {
        Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                surface.copy(alpha = 0.4f),
                surface.copy(alpha = 0.7f),
                surface.copy(alpha = 0.9f),
                surface,
            ),
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = TvManiacSpacing.small),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = TvManiacElevation.medium,
        onClick = { onItemClicked(item.showId) },
    ) {
        Box {
            PosterPlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(BackdropAspect),
                imageSize = 84.dp,
            )

            AsyncImageComposable(
                model = item.posterImageUrl,
                contentDescription = stringResource(cd_show_poster.resourceId, item.title),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(BackdropAspect),
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DetailedScrimHeight)
                    .align(Alignment.BottomCenter)
                    .background(scrim),
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(TvManiacSpacing.medium),
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                val metadata = buildMetadataString(item)
                if (metadata.isNotEmpty()) {
                    Text(
                        text = metadata,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun LibraryCompactItemPreview(
    @PreviewParameter(LibraryListItemPreviewParameterProvider::class) item: LibraryShowItem,
) {
    LibraryCompactItem(item = item, onItemClicked = {})
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun LibraryDetailedItemPreview(
    @PreviewParameter(LibraryListItemPreviewParameterProvider::class) item: LibraryShowItem,
) {
    LibraryDetailedItem(item = item, onItemClicked = {})
}
