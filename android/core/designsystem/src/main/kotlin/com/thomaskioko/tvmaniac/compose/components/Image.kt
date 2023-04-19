package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.load
import coil.request.ImageRequest
import com.flaviofaria.kenburnsview.KenBurnsView

@Composable
fun AsyncImageComposable(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    transform: (AsyncImagePainter.State) -> AsyncImagePainter.State = AsyncImagePainter.DefaultTransform,
    onState: ((AsyncImagePainter.State) -> Unit)? = null,
    requestBuilder: (ImageRequest.Builder.() -> ImageRequest.Builder)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
) {
    AsyncImage(
        model = requestBuilder?.let { builder ->
            when (model) {
                is ImageRequest -> model.newBuilder()
                else -> ImageRequest.Builder(LocalContext.current).data(model)
            }.apply { this.builder() }.build()
        } ?: model,
        contentDescription = contentDescription,
        modifier = modifier,
        transform = transform,
        onState = onState,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
    )
}

@Composable
fun KenBurnsViewImage(
    imageUrl: String?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val kenBuns = remember { KenBurnsView(context) }

    AndroidView({ kenBuns }, modifier = modifier) {
        it.load(imageUrl)
    }
}
