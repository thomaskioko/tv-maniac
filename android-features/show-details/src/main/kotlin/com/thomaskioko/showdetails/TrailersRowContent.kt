package com.thomaskioko.showdetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.TextLoadingItem
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Trailer
import com.thomaskioko.tvmaniac.resources.R
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun TrailersRowContent(
    isLoading: Boolean,
    trailersList: List<Trailer>,
    modifier: Modifier = Modifier,
    onTrailerClicked: (Long, String) -> Unit,
) {
    AnimatedVisibility(visible = trailersList.isNotEmpty()) {
        TextLoadingItem(
            isLoading = isLoading,
            text = stringResource(id = R.string.title_trailer),
        )
    }

    val lazyListState = rememberLazyListState()

    LazyRow(
        modifier = modifier,
        state = lazyListState,
        flingBehavior = rememberSnapperFlingBehavior(lazyListState),
    ) {
        itemsIndexed(trailersList) { index, trailer ->

            val value = if (index == 0) 16 else 8
            Spacer(modifier = Modifier.width(value.dp))

            Card(
                modifier = Modifier
                    .clickable { onTrailerClicked(trailer.showId, trailer.key) },
                shape = RoundedCornerShape(4.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp,
                ),
            ) {
                Box {
                    AsyncImageComposable(
                        model = trailer.youtubeThumbnailUrl,
                        contentDescription = trailer.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(140.dp)
                            .aspectRatio(3 / 1.5f)
                            .drawWithCache {
                                val gradient = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black),
                                    startY = size.height / 3,
                                    endY = size.height,
                                )
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(gradient, blendMode = BlendMode.Multiply)
                                }
                            },
                    )

                    Icon(
                        imageVector = Icons.Filled.PlayCircle,
                        contentDescription = trailer.name,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(48.dp),
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
fun TrailersContentPreview() {
    TvManiacTheme {
        Surface {
            TrailersRowContent(
                isLoading = false,
                trailersList = trailerLoaded.trailersList,
                onTrailerClicked = { _, _ -> },
            )
        }
    }
}
