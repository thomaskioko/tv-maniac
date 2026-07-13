package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.ImageType
import com.thomaskioko.tvmaniac.compose.theme.LocalPosterCornerRadius
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_show_poster

@Composable
public fun PosterBackdropCard(
    title: String,
    imageUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    contentScale: ContentScale = ContentScale.Crop,
    imageWidth: Dp = ImageType.Backdrop.width,
    aspectRatio: Float = ImageType.Backdrop.aspect,
    shape: Shape = RoundedCornerShape(LocalPosterCornerRadius.current),
) {
    val surface = MaterialTheme.colorScheme.surface
    val brush = remember(surface) {
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

    PosterCard(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        imageWidth = imageWidth,
        content = {
            Box {
                PosterPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(aspectRatio)
                        .align(Alignment.Center),
                    imageSize = 84.dp,
                )

                AsyncImageComposable(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(aspectRatio),
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
                        .background(brush),
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

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun PosterBackdropPreview() {
    PosterBackdropCard(
        imageUrl = "",
        title = "Game of Thrones",
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
    )
}
