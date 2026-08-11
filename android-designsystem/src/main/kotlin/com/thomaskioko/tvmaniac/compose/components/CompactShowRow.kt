package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.ImageType
import com.thomaskioko.tvmaniac.compose.theme.Layout
import com.thomaskioko.tvmaniac.compose.theme.TvManiacElevation
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing

private val PlaceholderIconSize = 24.dp

@Composable
public fun CompactShowRow(
    title: String,
    imageUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    metadata: AnnotatedString? = null,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = TvManiacSpacing.small),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = TvManiacElevation.medium,
        onClick = onClick,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // The artwork sits flush against the card edge and sets the row height.
            PosterCard(
                imageUrl = imageUrl,
                title = title,
                imageWidth = Layout.compactThumbnailWidth,
                aspectRatio = ImageType.Poster.aspect,
                placeholderIconSize = PlaceholderIconSize,
                shape = RectangleShape,
            )

            Column(
                modifier = Modifier.padding(TvManiacSpacing.small),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                if (!metadata.isNullOrEmpty()) {
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
private fun CompactShowRowPreview() {
    CompactShowRow(
        title = "Breaking Bad",
        metadata = metadataWithAccentDots(listOf("2008", "Ended", "5 Seasons")),
        imageUrl = null,
        onClick = {},
    )
}
