package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberImagePainter
import com.flaviofaria.kenburnsview.KenBurnsView
import com.thomaskioko.tvmaniac.compose.extensions.load

@Composable
fun NetworkImageComposable(
    imageUrl: String,
    modifier: Modifier,
    contentDescription: String
) {
    Image(
        painter = rememberImagePainter(data = imageUrl),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier,
        alignment = Alignment.TopStart,
    )
}

@Composable
fun KenBurnsViewImage(
    imageUrl: String,
    modifier: Modifier
){
    KenBurnsViewWrapper(
        url = imageUrl,
        modifier = modifier
    )
}

@Composable
fun KenBurnsViewWrapper(url: String, modifier: Modifier) {
    val context = LocalContext.current
    val kenBuns = remember { KenBurnsView(context) }

    AndroidView({ kenBuns }, modifier = modifier) {
        it.load(url = url)
    }
}