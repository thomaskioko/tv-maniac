@file:OptIn(ExperimentalFoundationApi::class)

package com.thomaskioko.tvmaniac.discover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thomaskioko.tvmaniac.category.api.model.Category
import com.thomaskioko.tvmaniac.compose.components.BoxTextItems
import com.thomaskioko.tvmaniac.compose.components.EmptyUi
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.RowError
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.compose.components.TvPosterCard
import com.thomaskioko.tvmaniac.compose.extensions.verticalGradientScrim
import com.thomaskioko.tvmaniac.compose.theme.MinContrastOfPrimaryVsSurface
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.theme.contrastAgainst
import com.thomaskioko.tvmaniac.compose.util.DominantColorState
import com.thomaskioko.tvmaniac.compose.util.DynamicThemePrimaryColorsFromImage
import com.thomaskioko.tvmaniac.compose.util.rememberDominantColorState
import com.thomaskioko.tvmaniac.navigation.extensions.viewModel
import com.thomaskioko.tvmaniac.presentation.discover.DataLoaded
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverState
import com.thomaskioko.tvmaniac.presentation.discover.Loading
import com.thomaskioko.tvmaniac.presentation.discover.RetryLoading
import com.thomaskioko.tvmaniac.presentation.discover.ShowsAction
import com.thomaskioko.tvmaniac.presentation.discover.model.TvShow
import com.thomaskioko.tvmaniac.resources.R
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import kotlin.math.absoluteValue

typealias Discover = @Composable (
    onShowClicked: (showId: Long) -> Unit,
    onMoreClicked: (showType: Long) -> Unit,
) -> Unit

@ExperimentalMaterialApi
@Inject
@Composable
fun Discover(
    viewModelFactory: () -> DiscoverViewModel,
    @Assisted onShowClicked: (showId: Long) -> Unit,
    @Assisted onMoreClicked: (showType: Long) -> Unit,
) {
    DiscoverScreen(
        viewModel = viewModel(factory = viewModelFactory),
        onShowClicked = onShowClicked,
        onMoreClicked = onMoreClicked,
    )
}

@ExperimentalMaterialApi
@Composable
internal fun DiscoverScreen(
    viewModel: DiscoverViewModel,
    onShowClicked: (showId: Long) -> Unit,
    modifier: Modifier = Modifier,
    onMoreClicked: (showType: Long) -> Unit,
) {
    val discoverViewState by viewModel.state.collectAsStateWithLifecycle()

    DiscoverScreen(
        state = discoverViewState,
        onShowClicked = onShowClicked,
        onReloadClicked = { viewModel.dispatch(it) },
        onRetry = { viewModel.dispatch(RetryLoading) },
        onMoreClicked = onMoreClicked,
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding()
            .animateContentSize(),
    )
}

@Composable
private fun DiscoverScreen(
    state: DiscoverState,
    onShowClicked: (showId: Long) -> Unit,
    onReloadClicked: (ShowsAction) -> Unit,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
    onMoreClicked: (showType: Long) -> Unit,
) {
    when (state) {
        Loading ->
            LoadingIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
            )

        is DataLoaded ->
            when {
                state.isContentEmpty -> {
                    EmptyUi(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                    )
                }

                state.isContentEmpty && state.errorMessage != null -> {
                    ErrorUi(
                        errorMessage = state.errorMessage,
                        onRetry = onRetry,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                    )
                }

                else -> {
                    DiscoverScrollContent(
                        modifier = modifier,
                        onShowClicked = onShowClicked,
                        onMoreClicked = onMoreClicked,
                        trendingShows = state.trendingShows,
                        popularShows = state.popularShows,
                        anticipatedShows = state.anticipatedShows,
                        recommendedShows = state.recommendedShows,
                    )
                }
            }
    }
}

@Composable
private fun DiscoverScrollContent(
    trendingShows: List<TvShow>?,
    popularShows: List<TvShow>?,
    anticipatedShows: List<TvShow>?,
    recommendedShows: List<TvShow>?,
    onShowClicked: (showId: Long) -> Unit,
    modifier: Modifier = Modifier,
    onMoreClicked: (showType: Long) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(
                WindowInsets.systemBars.only(WindowInsetsSides.Horizontal),
            ),
    ) {
        recommendedShows?.let {
            item {
                DiscoverHeaderContent(
                    showList = recommendedShows,
                    onShowClicked = onShowClicked,
                )
            }
        }

        trendingShows?.let {
            item {
                RowContent(
                    category = Category.TRENDING,
                    tvShows = trendingShows,
                    onItemClicked = onShowClicked,
                    onLabelClicked = onMoreClicked,
                )
            }
        }

        anticipatedShows?.let {
            item {
                RowContent(
                    category = Category.ANTICIPATED,
                    tvShows = anticipatedShows,
                    onItemClicked = onShowClicked,
                    onLabelClicked = onMoreClicked,
                )
            }
        }

        popularShows?.let {
            item {
                RowContent(
                    category = Category.POPULAR,
                    tvShows = popularShows,
                    onItemClicked = onShowClicked,
                    onLabelClicked = onMoreClicked,
                )
            }
        }
    }
}

@Composable
fun DiscoverHeaderContent(
    showList: List<TvShow>,
    modifier: Modifier = Modifier,
    onShowClicked: (Long) -> Unit,
) {
    Column(
        modifier = modifier
            .windowInsetsPadding(
                WindowInsets.systemBars.only(WindowInsetsSides.Horizontal),
            ),
    ) {
        val surfaceColor = MaterialTheme.colorScheme.surface
        val dominantColorState = rememberDominantColorState { color ->
            // We want a color which has sufficient contrast against the surface color
            color.contrastAgainst(surfaceColor) >= MinContrastOfPrimaryVsSurface
        }

        DynamicThemePrimaryColorsFromImage(dominantColorState) {
            val pagerState = rememberPagerState()
            val selectedImageUrl = showList.getOrNull(pagerState.currentPage)?.posterImageUrl

            // When the selected image url changes, call updateColorsFromImageUrl() or reset()
            LaunchedEffect(selectedImageUrl) {
                if (selectedImageUrl != null) {
                    dominantColorState.updateColorsFromImageUrl(selectedImageUrl)
                } else {
                    dominantColorState.reset()
                }
            }

            HorizontalPagerItem(
                list = showList,
                pagerState = pagerState,
                dominantColorState = dominantColorState,
                onClick = onShowClicked,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun HorizontalPagerItem(
    list: List<TvShow>,
    pagerState: PagerState,
    dominantColorState: DominantColorState,
    modifier: Modifier = Modifier,
    onClick: (Long) -> Unit,
) {
    val selectedImageUrl = list.getOrNull(pagerState.currentPage)?.posterImageUrl

    LaunchedEffect(selectedImageUrl) {
        if (selectedImageUrl != null) {
            dominantColorState.updateColorsFromImageUrl(selectedImageUrl)
        } else {
            dominantColorState.reset()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalGradientScrim(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                startYPercentage = 1f,
                endYPercentage = 0.5f,
            ),
    ) {
        HorizontalPager(
            pageCount = list.size,
            state = pagerState,
            beyondBoundsPageCount = 2,
            contentPadding = PaddingValues(horizontal = 45.dp),
            modifier = Modifier.fillMaxSize(),
        ) { pageNumber ->

            TvPosterCard(
                title = list[pageNumber].title,
                posterImageUrl = list[pageNumber].posterImageUrl,
                onClick = { onClick(list[pageNumber].traktId) },
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
                    .fillMaxWidth()
                    .aspectRatio(0.7f),
            )
        }

        if (list.isNotEmpty()) {
            LaunchedEffect(list) {
                if (list.size >= 4) {
                    pagerState.scrollToPage(2)
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

@OptIn(ExperimentalSnapperApi::class)
@Composable
private fun RowContent(
    category: Category,
    tvShows: List<TvShow>,
    onItemClicked: (Long) -> Unit,
    onLabelClicked: (Long) -> Unit,
) {
    AnimatedVisibility(visible = tvShows.isNotEmpty()) {
        Column {
            BoxTextItems(
                title = category.title,
                label = stringResource(id = R.string.str_more),
                onMoreClicked = { onLabelClicked(category.id) },
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
                        onClick = { onItemClicked(tvShow.traktId) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryError(
    categoryTitle: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column {
        BoxTextItems(
            title = categoryTitle,
        )

        RowError(
            modifier = modifier,
            onRetry = { onRetry() },
        )
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
                DiscoverScreen(
                    state = state,
                    onShowClicked = {},
                    onMoreClicked = {},
                    onReloadClicked = {},
                )
            }
        }
    }
}
