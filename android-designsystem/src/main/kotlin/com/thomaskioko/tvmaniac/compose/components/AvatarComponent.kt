package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
public fun AvatarComponent(
    imageUrl: String?,
    size: Dp,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    border: BorderStroke? = null,
    placeholderIcon: ImageVector = Icons.Outlined.Person,
    onClick: (() -> Unit)? = null,
) {
    val commonModifier = modifier
        .size(size)
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)

    if (imageUrl.isNullOrEmpty()) {
        Box(
            modifier = commonModifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape,
                )
                .then(if (border != null) Modifier.border(border, CircleShape) else Modifier),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = placeholderIcon,
                contentDescription = contentDescription,
                modifier = Modifier.size(size * 0.6f),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    } else {
        AsyncImageComposable(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = commonModifier,
            shape = CircleShape,
            border = border,
            contentScale = ContentScale.Crop,
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun AvatarComponentPreview() {
    AvatarComponent(
        imageUrl = "https://image.png",
        size = 64.dp,
        modifier = Modifier.padding(16.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun AvatarComponentPlaceholderPreview() {
    AvatarComponent(
        modifier = Modifier.wrapContentSize(),
        imageUrl = "",
        size = 38.dp,
        contentDescription = "Profile",
        onClick = {},
    )
}
