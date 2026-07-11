package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.ImageType
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_show_poster

@Composable
public fun PosterCard(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    title: String? = null,
    imageWidth: Dp = ImageType.Poster.width,
    aspectRatio: Float = ImageType.Poster.aspect,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RectangleShape,
    isInLibrary: Boolean = false,
    libraryImageOverlay: ImageVector = Icons.Filled.Bookmarks,
    blurContent: Boolean = false,
) {
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
                    title = title,
                )

                AsyncImageComposable(
                    model = imageUrl,
                    contentScale = contentScale,
                    contentDescription = title?.let {
                        stringResource(
                            cd_show_poster.resourceId,
                            title,
                        )
                    },
                    aspectRatio = aspectRatio,
                    modifier = Modifier.fillMaxWidth(),
                    blurContent = blurContent,
                )

                if (isInLibrary) {
                    LibraryOverlay(libraryImageOverlay = libraryImageOverlay)
                }
            }
        },
    )
}

@Composable
private fun LibraryOverlay(
    libraryImageOverlay: ImageVector,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopEnd,
    ) {
        Icon(
            imageVector = libraryImageOverlay,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .padding(8.dp)
                .size(20.dp),
        )
    }
}

@Composable
internal fun PosterCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageWidth: Dp = ImageType.Poster.width,
    shape: Shape = RectangleShape,
    content: @Composable () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .width(imageWidth),
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
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun PosterCardPreview() {
    PosterCard(
        imageUrl = "",
        title = "Loki",
        modifier = Modifier
            .width(100.dp)
            .aspectRatio(0.8f),
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun PosterCardWithLibraryOverlayPreview() {
    PosterCard(
        imageUrl = "",
        title = "Loki",
        isInLibrary = true,
        modifier = Modifier
            .width(100.dp)
            .aspectRatio(0.8f),
    )
}
