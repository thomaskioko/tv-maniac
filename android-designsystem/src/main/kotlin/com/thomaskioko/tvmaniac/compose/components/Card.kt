package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_show_poster

@Composable
fun PosterCard(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    title: String? = null,
    imageWidth: Dp = 120.dp,
    aspectRatio: Float = 2 / 3f,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = MaterialTheme.shapes.small,
    onClick: () -> Unit = {},
) {
    PosterCard(
        modifier = modifier,
        shape = shape,
        imageWidth = imageWidth,
        onClick = onClick,
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                PlaceholderContent(
                    modifier = Modifier.align(Alignment.Center),
                    imageUrl = imageUrl,
                    title = title,
                )

                AsyncImageComposable(
                    model = imageUrl,
                    contentScale = contentScale,
                    contentDescription = title?.let { stringResource(cd_show_poster.resourceId, title) },
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(aspectRatio),
                )
            }
        },
    )
}

@Composable
private fun PlaceholderContent(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    imageSize: Dp = 52.dp,
    title: String? = null,
) {
    Column(
        modifier = modifier,
    ) {
        imageUrl?.let {
            Icon(
                modifier = Modifier
                    .size(imageSize)
                    .padding(4.dp)
                    .align(Alignment.CenterHorizontally),
                imageVector = Icons.Outlined.Movie,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            )
        }

        title?.let {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun PosterBackdropCard(
    title: String,
    imageUrl: String?,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    contentScale: ContentScale = ContentScale.Crop,
    imageWidth: Dp = 120.dp,
    shape: Shape = MaterialTheme.shapes.small,
    onClick: () -> Unit,
) {
    val surface = MaterialTheme.colorScheme.surface
    val overlayGradient = listOf(
        Color.Transparent,
        surface.copy(alpha = 0.4f),
        surface.copy(alpha = 0.7f),
        surface.copy(alpha = 0.9f),
        surface,
    )

    PosterCard(
        modifier = modifier,
        shape = shape,
        imageWidth = imageWidth,
        onClick = onClick,
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                PlaceholderContent(
                    modifier = Modifier.align(Alignment.Center),
                    imageUrl = imageUrl,
                    imageSize = 84.dp,
                )

                AsyncImageComposable(
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(2 / 3f),
                    model = imageUrl,
                    contentScale = contentScale,
                    contentDescription = stringResource(cd_show_poster.resourceId, title),
                    alignment = Alignment.Center,
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .align(Alignment.BottomCenter)
                        .background(Brush.verticalGradient(overlayGradient)),
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = textAlign,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomStart),
                )
            }
        },
    )
}

@Composable
internal fun PosterCard(
    modifier: Modifier = Modifier,
    imageWidth: Dp = 120.dp,
    shape: Shape = MaterialTheme.shapes.small,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier
            .width(imageWidth)
            .clickable { onClick() },
        shape = shape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        content()
    }
}

@ThemePreviews
@Composable
private fun PosterCardPreview() {
    TvManiacTheme {
        Surface {
            PosterCard(
                imageUrl = "",
                title = "Loki",
                onClick = {},
                modifier = Modifier
                    .width(100.dp)
                    .aspectRatio(0.8f),
            )
        }
    }
}

@ThemePreviews
@Composable
private fun PosterBackdropPreview() {
    TvManiacTheme {
        Surface {
            PosterBackdropCard(
                imageUrl = "",
                title = "Game of Thrones",
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
            )
        }
    }
}
