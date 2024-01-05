@file:OptIn(ExperimentalFoundationApi::class)

package com.thomaskioko.tvmaniac.discover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.thomaskioko.tvmaniac.compose.components.BoxTextItems
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.compose.components.TvPosterCard
import com.thomaskioko.tvmaniac.compose.extensions.verticalGradientScrim
import com.thomaskioko.tvmaniac.compose.theme.MinContrastOfPrimaryVsSurface
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.theme.contrastAgainst
import com.thomaskioko.tvmaniac.compose.util.DynamicThemePrimaryColorsFromImage
import com.thomaskioko.tvmaniac.compose.util.rememberDominantColorState
import com.thomaskioko.tvmaniac.presentation.discover.DataLoaded
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverShowAction
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverState
import com.thomaskioko.tvmaniac.presentation.discover.ErrorState
import com.thomaskioko.tvmaniac.presentation.discover.Loading
import com.thomaskioko.tvmaniac.presentation.discover.PopularClicked
import com.thomaskioko.tvmaniac.presentation.discover.RetryLoading
import com.thomaskioko.tvmaniac.presentation.discover.ShowClicked
import com.thomaskioko.tvmaniac.presentation.discover.SnackBarDismissed
import com.thomaskioko.tvmaniac.presentation.discover.TopRatedClicked
import com.thomaskioko.tvmaniac.presentation.discover.TrendingClicked
import com.thomaskioko.tvmaniac.presentation.discover.UpComingClicked
import com.thomaskioko.tvmaniac.presentation.discover.model.DiscoverShow
import com.thomaskioko.tvmaniac.resources.R
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.collections.immutable.ImmutableList
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiscoverScreen(
    discoverShowsPresenter: DiscoverShowsPresenter,
    modifier: Modifier = Modifier,
) {
    val discoverState by discoverShowsPresenter.state.subscribeAsState()
    val pagerState = rememberPagerState(
        initialPage = 2,
        pageCount = { (discoverState as? DataLoaded)?.featuredShows?.size ?: 0 }
    )
    val snackBarHostState = remember { SnackbarHostState() }

    DiscoverScreen(
        modifier = modifier,
        state = discoverState,
        snackBarHostState = snackBarHostState,
        pagerState = pagerState,
        onAction = discoverShowsPresenter::dispatch,
    )
}

@Composable
internal fun DiscoverScreen(
    state: DiscoverState,
    snackBarHostState: SnackbarHostState,
    pagerState: PagerState,
    onAction: (DiscoverShowAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        Loading -> LoadingIndicator(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
        )

        is DataLoaded -> DiscoverScrollContent(
            modifier = modifier,
            pagerState = pagerState,
            snackBarHostState = snackBarHostState,
            topRatedShows = state.topRatedShows,
            popularShows = state.popularShows,
            upcomingShows = state.upcomingShows,
            featuredShows = state.featuredShows,
            trendingToday = state.trendingToday,
            errorMessage = state.errorMessage,
            onAction = onAction,
        )

        is ErrorState -> ErrorUi(
            errorMessage = state.errorMessage,
            onRetry = { onAction(RetryLoading) },
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
        )
    }
}

@Composable
private fun DiscoverScrollContent(
    topRatedShows: ImmutableList<DiscoverShow>,
    popularShows: ImmutableList<DiscoverShow>,
    upcomingShows: ImmutableList<DiscoverShow>,
    featuredShows: ImmutableList<DiscoverShow>?,
    trendingToday: ImmutableList<DiscoverShow>,
    errorMessage: String?,
    snackBarHostState: SnackbarHostState,
    pagerState: PagerState,
    onAction: (DiscoverShowAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(key1 = errorMessage) {
        errorMessage?.let {
            val snackBarResult = snackBarHostState.showSnackbar(
                message = errorMessage,
                duration = SnackbarDuration.Short,
            )
            when (snackBarResult) {
                SnackbarResult.ActionPerformed, SnackbarResult.Dismissed ->
                    onAction(SnackBarDismissed)
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)),
        ) {
            featuredShows?.let {
                item {
                    DiscoverHeaderContent(
                        pagerState = pagerState,
                        showList = featuredShows,
                        onShowClicked = { onAction(ShowClicked(it)) },
                    )
                }
            }

            item {
                HorizontalRowContent(
                    category = stringResource(id = R.string.title_category_upcoming),
                    tvShows = upcomingShows,
                    onItemClicked = { onAction(ShowClicked(it)) },
                    onMoreClicked = { onAction(UpComingClicked) },
                )
            }

            item {
                HorizontalRowContent(
                    category = stringResource(id = R.string.title_category_trending_today),
                    tvShows = trendingToday,
                    onItemClicked = { onAction(ShowClicked(it)) },
                    onMoreClicked = { onAction(TrendingClicked) },
                )
            }

            item {
                HorizontalRowContent(
                    category = stringResource(id = R.string.title_category_popular),
                    tvShows = popularShows,
                    onItemClicked = { onAction(ShowClicked(it)) },
                    onMoreClicked = { onAction(PopularClicked) },
                )
            }

            item {
                HorizontalRowContent(
                    category = stringResource(id = R.string.title_category_top_rated),
                    tvShows = topRatedShows,
                    onItemClicked = { onAction(ShowClicked(it)) },
                    onMoreClicked = { onAction(TopRatedClicked) },
                )
            }
        }

        SnackbarHost(hostState = snackBarHostState)
    }
}

@Composable
fun DiscoverHeaderContent(
    showList: ImmutableList<DiscoverShow>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    onShowClicked: (Long) -> Unit,
) {
    val selectedImageUrl = showList.getOrNull(pagerState.currentPage)?.posterImageUrl

    DynamicColorContainer(selectedImageUrl) {
        Column(
            modifier = modifier
                .windowInsetsPadding(
                    WindowInsets.systemBars.only(WindowInsetsSides.Horizontal),
                ),
        ) {
            val backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)

            HorizontalPagerItem(
                list = showList,
                pagerState = pagerState,
                backgroundColor = backgroundColor,
                onClick = onShowClicked,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DynamicColorContainer(
    selectedImageUrl: String?,
    content: @Composable () -> Unit,
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val dominantColorState = rememberDominantColorState { color ->
        // We want a color which has sufficient contrast against the surface color
        color.contrastAgainst(surfaceColor) >= MinContrastOfPrimaryVsSurface
    }

    DynamicThemePrimaryColorsFromImage(dominantColorState) {
        // When the selected image url changes, call updateColorsFromImageUrl() or reset()
        LaunchedEffect(selectedImageUrl) {
            if (selectedImageUrl != null) {
                dominantColorState.updateColorsFromImageUrl(selectedImageUrl)
            } else {
                dominantColorState.reset()
            }
        }

        content()
    }
}

@Composable
fun HorizontalPagerItem(
    list: ImmutableList<DiscoverShow>,
    pagerState: PagerState,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: (Long) -> Unit,
) {
    Column(
        modifier = modifier
            .windowInsetsPadding(
                WindowInsets.systemBars.only(WindowInsetsSides.Horizontal),
            )
            .fillMaxWidth()
            .verticalGradientScrim(
                color = backgroundColor,
                startYPercentage = 1f,
                endYPercentage = 0.5f,
            )
            .padding(top = 84.dp),
    ) {
        HorizontalPager(
            state = pagerState,
            beyondBoundsPageCount = 2,
            contentPadding = PaddingValues(horizontal = 45.dp),
            modifier = Modifier.fillMaxSize(),
        ) { pageNumber ->

            TvPosterCard(
                title = list[pageNumber].title,
                posterImageUrl = list[pageNumber].posterImageUrl,
                onClick = { onClick(list[pageNumber].tmdbId) },
                modifier = Modifier
                    .graphicsLayer {
                        val pageOffset = (
                                (pagerState.currentPage - pageNumber) + pagerState
                                    .currentPageOffsetFraction
                                ).absoluteValue

                        // We animate the scaleX + scaleY, between 85% and 100%
                        lerp(
                            start = 0.85f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f),
                        ).also { scale ->
                            scaleX = scale
                            scaleY = scale
                        }

                        // We animate the alpha, between 50% and 100%
                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f),
                        )
                    }
                    .fillMaxWidth(),
            )
        }

        if (list.isNotEmpty()) {
            LaunchedEffect(pagerState) {
                snapshotFlow { pagerState.currentPage }.collect { page ->
                    pagerState.scrollToPage(page)
                }
            }

            Row(
                Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(list.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    }

                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSnapperApi::class, ExperimentalFoundationApi::class)
@Composable
private fun HorizontalRowContent(
    category: String,
    tvShows: ImmutableList<DiscoverShow>,
    onItemClicked: (Long) -> Unit,
    onMoreClicked: () -> Unit,
) {
    AnimatedVisibility(visible = tvShows.isNotEmpty()) {
        Column {
            BoxTextItems(
                title = category,
                label = stringResource(id = R.string.str_more),
                onMoreClicked = onMoreClicked,
            )

            val lazyListState = rememberLazyListState()

            LazyRow(
                state = lazyListState,
                flingBehavior = rememberSnapperFlingBehavior(lazyListState),
            ) {
                itemsIndexed(tvShows) { index, tvShow ->

                    val value = if (index == 0) 16 else 8

                    Spacer(modifier = Modifier.width(value.dp))

                    TvPosterCard(
                        posterImageUrl = tvShow.posterImageUrl,
                        title = tvShow.title,
                        onClick = { onItemClicked(tvShow.tmdbId) },
                        modifier = Modifier
                            .wrapContentHeight()
                            .animateItemPlacement(),
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun DiscoverScreenPreview(
    @PreviewParameter(DiscoverPreviewParameterProvider::class)
    state: DiscoverState,
) {
    TvManiacTheme {
        TvManiacBackground {
            Surface(Modifier.fillMaxWidth()) {
                val pagerState = rememberPagerState(pageCount = { 5 })
                val snackBarHostState = remember { SnackbarHostState() }
                DiscoverScreen(
                    state = state,
                    pagerState = pagerState,
                    snackBarHostState = snackBarHostState,
                    onAction = {},
                )
            }
        }
    }
}
