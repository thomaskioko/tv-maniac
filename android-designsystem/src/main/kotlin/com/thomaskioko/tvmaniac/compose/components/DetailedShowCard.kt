package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.TvManiacElevation
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_show_poster

private val BackdropAspect = 16f / 9f
private val ScrimHeight = 96.dp
private val ProgressHeight = 8.dp

@Composable
public fun DetailedShowCard(
    title: String,
    imageUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: AnnotatedString? = null,
    progress: Float? = null,
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
        onClick = onClick,
    ) {
        Box {
            PosterPlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(BackdropAspect),
                imageSize = 84.dp,
            )

            AsyncImageComposable(
                model = imageUrl,
                contentDescription = stringResource(cd_show_poster.resourceId, title),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(BackdropAspect),
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ScrimHeight)
                    .align(Alignment.BottomCenter)
                    .background(scrim),
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(TvManiacSpacing.medium),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                if (!subtitle.isNullOrEmpty()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            progress?.let {
                ShowLinearProgressIndicator(
                    progress = it,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(ProgressHeight),
                )
            }
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun DetailedShowCardPreview() {
    DetailedShowCard(
        title = "Breaking Bad",
        subtitle = metadataWithAccentDots(listOf("S05E14", "Ozymandias")),
        imageUrl = null,
        progress = 0.6f,
        onClick = {},
    )
}
