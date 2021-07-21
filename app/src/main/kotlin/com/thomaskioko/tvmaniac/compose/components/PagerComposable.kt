package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowsEntity
import kotlin.math.absoluteValue


@Composable
fun HorizontalPager(
    list: List<TvShowsEntity>,
    onClick: (Int) -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = list.size,
        initialOffscreenLimit = 2,
    )

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
    ) { pageNumber ->

        Card(
            Modifier
                .clickable { onClick(list[pageNumber].id) }
                .graphicsLayer {
                    val pageOffset = calculateCurrentOffsetForPage(pageNumber).absoluteValue

                    // We animate the scaleX + scaleY, between 85% and 100%
                    lerp(
                        start = 0.85f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    ).also { scale ->
                        scaleX = scale
                        scaleY = scale
                    }

                    // We animate the alpha, between 50% and 100%
                    alpha = lerp(
                        start = 0.5f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                }
                .fillMaxWidth(0.8f)
                .aspectRatio(1f)
        ) {
            Box {
                NetworkImageComposable(
                    imageUrl = list[pageNumber].posterImageUrl,
                    modifier = Modifier
                        .fillMaxSize()
                        .height(250.dp)
                )
            }
        }
    }
}