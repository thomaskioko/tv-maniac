package com.thomaskioko.tvmaniac.discover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.thomaskioko.tvmaniac.category.api.model.Category
import com.thomaskioko.tvmaniac.compose.components.BoxTextItems
import com.thomaskioko.tvmaniac.compose.components.EmptyContent
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.RowError
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.compose.components.TvPosterCard
import com.thomaskioko.tvmaniac.compose.extensions.verticalGradientScrim
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.theme.contrastAgainst
import com.thomaskioko.tvmaniac.compose.util.DominantColorState
import com.thomaskioko.tvmaniac.compose.util.DynamicThemePrimaryColorsFromImage
import com.thomaskioko.tvmaniac.compose.util.rememberDominantColorState
import com.thomaskioko.tvmaniac.navigation.extensions.viewModel
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.shared.domain.discover.Loading
import com.thomaskioko.tvmaniac.shared.domain.discover.LoadingError
import com.thomaskioko.tvmaniac.shared.domain.discover.ReloadFeatured
import com.thomaskioko.tvmaniac.shared.domain.discover.RetryLoading
import com.thomaskioko.tvmaniac.shared.domain.discover.ShowResult
import com.thomaskioko.tvmaniac.shared.domain.discover.ShowResult.CategorySuccess
import com.thomaskioko.tvmaniac.shared.domain.discover.ShowsAction
import com.thomaskioko.tvmaniac.shared.domain.discover.ShowsLoaded
import com.thomaskioko.tvmaniac.shared.domain.discover.ShowsState
import com.thomaskioko.tvmaniac.shared.domain.discover.model.TvShow
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import kotlin.math.absoluteValue

typealias Discover = @Composable (
    onShowClicked: (showId: Long) -> Unit,
    onMoreClicked: (showType: Long) -> Unit,
) -> Unit

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
    state: ShowsState,
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

        is ShowsLoaded ->
            CategoryContent(
                state = state,
                onReloadClicked = onReloadClicked,
                modifier = modifier,
                onShowClicked = onShowClicked,
                onMoreClicked = onMoreClicked,
            )

        is LoadingError ->
            ErrorUi(
                errorMessage = state.errorMessage,
                onRetry = onRetry,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
            )
    }
}

@Composable
private fun CategoryContent(
    state: ShowsLoaded,
    onReloadClicked: (ShowsAction) -> Unit,
    onShowClicked: (showId: Long) -> Unit,
    modifier: Modifier = Modifier,
    onMoreClicked: (showType: Long) -> Unit,
) {
    LazyColumn {
        item {
            when (state.result.featuredCategoryState) {
                is ShowResult.CategoryError ->
                    CategoryError(
                        categoryTitle = Category.FEATURED.title,
                        onRetry = { onReloadClicked(ReloadFeatured) },
                        modifier = modifier
                            .fillMaxWidth()
                            .aspectRatio(0.7f),
                    )

                is CategorySuccess -> {
                    val resultState = (state.result.featuredCategoryState as CategorySuccess)

                    FeaturedContent(
                        showList = resultState.tvShows,
                        onShowClicked = onShowClicked,
                        modifier = modifier,
                    )
                }

                ShowResult.EmptyCategoryData ->
                    EmptyContent(
                        painter = painterResource(id = R.drawable.ic_watchlist_empty),
                        message = stringResource(id = R.string.generic_empty_content),
                    )
            }
        }

        item {
            when (state.result.trendingCategoryState) {
                is ShowResult.CategoryError ->
                    CategoryError(
                        categoryTitle = Category.TRENDING.title,
                        onRetry = { onReloadClicked(ReloadFeatured) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize()
                            .padding(vertical = 16.dp),
                    )

                is CategorySuccess -> {
                    val resultState = (state.result.trendingCategoryState as CategorySuccess)
                    RowContent(
                        category = resultState.category,
                        tvShows = resultState.tvShows,
                        onItemClicked = onShowClicked,
                        onLabelClicked = onMoreClicked,
                    )
                }

                ShowResult.EmptyCategoryData ->
                    EmptyContent(
                        painter = painterResource(id = R.drawable.ic_watchlist_empty),
                        message = stringResource(id = R.string.generic_empty_content),
                    )
            }
        }

        item {
            when (state.result.anticipatedCategoryState) {
                is ShowResult.CategoryError ->
                    CategoryError(
                        categoryTitle = Category.ANTICIPATED.title,
                        onRetry = { onReloadClicked(ReloadFeatured) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize()
                            .padding(vertical = 16.dp),
                    )

                is CategorySuccess -> {
                    val resultState = (state.result.anticipatedCategoryState as CategorySuccess)
                    RowContent(
                        category = resultState.category,
                        tvShows = resultState.tvShows,
                        onItemClicked = onShowClicked,
                        onLabelClicked = onMoreClicked,
                    )
                }

                ShowResult.EmptyCategoryData ->
                    EmptyContent(
                        painter = painterResource(id = R.drawable.ic_watchlist_empty),
                        message = stringResource(id = R.string.generic_empty_content),
                    )
            }
        }

        item {
            when (state.result.popularCategoryState) {
                is ShowResult.CategoryError ->
                    CategoryError(
                        categoryTitle = Category.POPULAR.title,
                        onRetry = { onReloadClicked(ReloadFeatured) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize()
                            .padding(vertical = 16.dp),
                    )

                is CategorySuccess -> {
                    val resultState = (state.result.popularCategoryState as CategorySuccess)
                    RowContent(
                        category = resultState.category,
                        tvShows = resultState.tvShows,
                        onItemClicked = onShowClicked,
                        onLabelClicked = onMoreClicked,
                    )
                }

                ShowResult.EmptyCategoryData ->
                    EmptyContent(
                        painter = painterResource(id = R.drawable.ic_watchlist_empty),
                        message = stringResource(id = R.string.generic_empty_content),
                    )
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun FeaturedContent(
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
        val surfaceColor = MaterialTheme.colorScheme.secondary
        val dominantColorState = rememberDominantColorState { color ->
            // We want a color which has sufficient contrast against the surface color
            color.contrastAgainst(surfaceColor) >= 3f
        }
        val pagerState = rememberPagerState()

        DynamicThemePrimaryColorsFromImage(dominantColorState) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalGradientScrim(
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.38f),
                        startYPercentage = 1f,
                        endYPercentage = 0f,
                    ),
            ) {
                Spacer(modifier = Modifier.height(90.dp))

                HorizontalPagerItem(
                    list = showList,
                    pagerState = pagerState,
                    dominantColorState = dominantColorState,
                    onClick = onShowClicked,
                )
            }
        }

        if (showList.isNotEmpty()) {
            HorizontalPagerIndicator(
                pagerState = pagerState,
                activeColor = MaterialTheme.colorScheme.secondary,
                inactiveColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalPagerApi::class)
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

    LaunchedEffect(list) {
        if (list.size >= 4) {
            pagerState.scrollToPage(2)
        }
    }

    HorizontalPager(
        count = list.size,
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 45.dp),
        modifier = modifier
            .fillMaxSize(),
    ) { pageNumber ->

        TvPosterCard(
            title = list[pageNumber].title,
            posterImageUrl = list[pageNumber].posterImageUrl,
            onClick = { onClick(list[pageNumber].traktId) },
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    val pageOffset = calculateCurrentOffsetForPage(pageNumber).absoluteValue

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
                .offset {
                    val pageOffset = calculateCurrentOffsetForPage(pageNumber)
                    // Then use it as a multiplier to apply an offset
                    IntOffset(
                        x = (37.dp * pageOffset).roundToPx(),
                        y = 0,
                    )
                }
                .fillMaxWidth()
                .aspectRatio(0.7f),
        )
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

                    val value = if (index == 0) 16 else 4

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
    state: ShowsState,
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
