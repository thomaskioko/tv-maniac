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
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_show_poster

@Composable
public fun PosterCard(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    title: String? = null,
    imageWidth: Dp = 120.dp,
    aspectRatio: Float = 2 / 3f,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RectangleShape,
    isInLibrary: Boolean = false,
    libraryImageOverlay: ImageVector = Icons.Filled.Bookmarks,
    onClick: () -> Unit = {},
) {
    PosterCard(
        modifier = modifier,
        shape = shape,
        imageWidth = imageWidth,
        onClick = onClick,
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(aspectRatio),
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
public fun PosterBackdropCard(
    title: String,
    imageUrl: String?,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    contentScale: ContentScale = ContentScale.Crop,
    imageWidth: Dp = 120.dp,
    aspectRatio: Float = 2 / 3f,
    shape: Shape = RectangleShape,
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
    imageWidth: Dp,
    shape: Shape,
    onClick: () -> Unit,
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

@Composable
public fun CastCard(
    profileUrl: String?,
    name: String,
    characterName: String,
    modifier: Modifier = Modifier,
    height: Dp = 160.dp,
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .size(width = 120.dp, height = height),
            contentAlignment = Alignment.BottomStart,
        ) {
            CastPlaceholder(
                modifier = Modifier.fillMaxSize(),
                imageUrl = profileUrl,
                name = name,
            )

            AsyncImageComposable(
                model = profileUrl,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(contentBackgroundGradient()),
            )

            CastNameOverlay(
                name = name,
                characterName = characterName,
            )
        }
    }
}

@Composable
private fun CastPlaceholder(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    name: String? = null,
) {
    if (imageUrl.isNullOrEmpty()) {
        Box(
            modifier = modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Gray.copy(alpha = 0.8f),
                            Color.Gray,
                        ),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.size(52.dp),
                imageVector = Icons.Outlined.Person,
                contentDescription = name,
                tint = Color.White.copy(alpha = 0.8f),
            )
        }
    }
}

@Composable
private fun CastNameOverlay(
    name: String,
    characterName: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(8.dp)) {
        Text(
            text = name,
            modifier = Modifier
                .padding(vertical = 2.dp)
                .fillMaxWidth(),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            ),
        )
        Text(
            text = characterName,
            modifier = Modifier.fillMaxWidth(),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
            ),
        )
    }
}

@Composable
private fun contentBackgroundGradient(): Brush {
    val surface = MaterialTheme.colorScheme.surface
    return Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            surface.copy(alpha = 0.3f),
            surface.copy(alpha = 0.6f),
            surface.copy(alpha = 0.9f),
            surface,
        ),
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun CastCardPreview() {
    CastCard(
        profileUrl = null,
        name = "Tom Hiddleston",
        characterName = "Loki",
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun PosterCardPreview() {
    PosterCard(
        imageUrl = "",
        title = "Loki",
        onClick = {},
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
        onClick = {},
        modifier = Modifier
            .width(100.dp)
            .aspectRatio(0.8f),
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

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun AvatarComponentPreview() {
    AvatarComponent(
        imageUrl = "",
        size = 38.dp,
        contentDescription = "Profile",
        onClick = {},
    )
}
