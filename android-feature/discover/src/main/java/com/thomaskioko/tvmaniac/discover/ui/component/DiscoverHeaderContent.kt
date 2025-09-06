package com.thomaskioko.tvmaniac.discover.ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.ExpandingText
import com.thomaskioko.tvmaniac.compose.components.ParallaxCarouselImage
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.discover.presenter.model.DiscoverShow
import com.thomaskioko.tvmaniac.discover.ui.discoverContentSuccess
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay

@Composable
internal fun DiscoverHeaderContent(
    showList: ImmutableList<DiscoverShow>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    onShowClicked: (Long) -> Unit,
) {
    Column(
        modifier = modifier.windowInsetsPadding(
            WindowInsets.systemBars.only(WindowInsetsSides.Horizontal),
        ),
    ) {
        PosterCardsPager(
            pagerState = pagerState,
            list = showList,
            onClick = onShowClicked,
        )
    }
}

@Composable
internal fun PosterCardsPager(
    pagerState: PagerState,
    list: ImmutableList<DiscoverShow>,
    modifier: Modifier = Modifier,
    onClick: (Long) -> Unit,
) {
    val memoizedOnClick = remember(onClick) { onClick }
    if (list.isEmpty()) return

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val pagerHeight = screenHeight / 1.5f
    Box {
        HorizontalPager(
            modifier = modifier
                .fillMaxWidth()
                .height(pagerHeight),
            state = pagerState,
            verticalAlignment = Alignment.Bottom,
        ) { currentPage ->

            val currentShow = remember(list, currentPage) { list[currentPage] }
            ParallaxCarouselImage(
                state = pagerState,
                currentPage = currentPage,
                imageUrl = currentShow.posterImageUrl,
                modifier = Modifier
                    .clickable(onClick = { memoizedOnClick(currentShow.tmdbId) }),
            ) {
                ShowCardOverlay(
                    title = currentShow.title,
                    overview = currentShow.overView,
                )
            }
        }

        if (list.isNotEmpty()) {
            LaunchedEffect(key1 = list.size) {
                while (true) {
                    delay(4_500)

                    // Animate to next page with custom animation spec
                    val nextPage = if (pagerState.currentPage + 1 < list.size) {
                        pagerState.currentPage + 1
                    } else {
                        0
                    }

                    pagerState.animateScrollToPage(
                        page = nextPage,
                        animationSpec = tween(
                            durationMillis = 800,
                            easing = FastOutSlowInEasing,
                        ),
                    )
                }
            }

            CircularIndicator(
                modifier = Modifier.align(Alignment.BottomCenter),
                totalItems = list.size,
                currentPage = pagerState.currentPage,
                isUserScrolling = pagerState.isScrollInProgress,
            )
        }
    }
}

@Composable
private fun ShowCardOverlay(
    title: String,
    overview: String?,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                    startY = 500f,
                    endY = 1000f,
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = -(20).dp)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSecondary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )

            Spacer(modifier = Modifier.height(8.dp))

            overview?.let {
                ExpandingText(
                    text = overview,
                    textStyle = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSecondary,
                )
            }
        }
    }
}

@ThemePreviews
@Composable
internal fun DiscoverHeaderContentPreview() {
    val pagerState = rememberPagerState(pageCount = { 5 })
    TvManiacTheme {
        DiscoverHeaderContent(
            showList = discoverContentSuccess.featuredShows,
            pagerState = pagerState,
            onShowClicked = {},
        )
    }
}
