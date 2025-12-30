package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.times
import androidx.compose.ui.util.lerp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.load
import coil.request.ImageRequest
import com.flaviofaria.kenburnsview.KenBurnsView
import kotlin.math.absoluteValue

@Composable
public fun AsyncImageComposable(
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
            }
                .apply { this.builder() }
                .build()
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
public fun KenBurnsViewImage(
    imageUrl: String?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val kenBuns = remember { KenBurnsView(context) }

    AndroidView({ kenBuns }, modifier = modifier) { it.load(imageUrl) }
}

@Composable
public fun ParallaxCarouselImage(
    state: PagerState,
    currentPage: Int,
    imageUrl: String?,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    overlayContent: @Composable () -> Unit = {},
) {
    val currentPageOffset = calculatePageOffset(state, currentPage)
    val cardTranslationX = lerp(100f, 0f, 1f - currentPageOffset)
    val cardScaleX = lerp(0.8f, 1f, 1f - currentPageOffset.absoluteValue.coerceIn(0f, 1f))
    val density = LocalDensity.current
    val screenWidth = with(density) {
        LocalWindowInfo.current.containerSize.width.toDp()
    }
    val parallaxOffset = currentPageOffset * screenWidth * 2f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = cardScaleX
                translationX = cardTranslationX
            },
    ) {
        AsyncImageComposable(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .graphicsLayer {
                    translationX = lerp(10f, 0f, 1f - currentPageOffset) + parallaxOffset.value
                },
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )

        overlayContent()
    }
}

private fun calculatePageOffset(state: PagerState, currentPage: Int): Float {
    return (state.currentPage + state.currentPageOffsetFraction - currentPage).coerceIn(-1f, 1f)
}
