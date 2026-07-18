package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.ImageType
import com.thomaskioko.tvmaniac.compose.theme.TvManiacElevation
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing

@Composable
public fun CastCard(
    profileUrl: String?,
    name: String,
    characterName: String,
    modifier: Modifier = Modifier,
    width: Dp = ImageType.Cast.width,
    height: Dp = width / ImageType.Cast.aspect,
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.cardElevation(defaultElevation = TvManiacElevation.large),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .size(width = width, height = height),
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
    Column(modifier = modifier.padding(TvManiacSpacing.xSmall)) {
        Text(
            text = name,
            modifier = Modifier
                .padding(vertical = TvManiacSpacing.xxxSmall)
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
    return remember(surface) {
        Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                surface.copy(alpha = 0.3f),
                surface.copy(alpha = 0.6f),
                surface.copy(alpha = 0.9f),
                surface,
            ),
        )
    }
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
