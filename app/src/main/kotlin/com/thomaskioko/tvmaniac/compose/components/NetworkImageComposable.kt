package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberImagePainter

@Composable
fun NetworkImageComposable(
    imageUrl: String,
    modifier: Modifier
) {
    Image(
        painter = rememberImagePainter(data = imageUrl),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}