package com.thomaskioko.tvmaniac.discover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.components.EmptyContentView
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.components.FullScreenLoading
import com.thomaskioko.tvmaniac.compose.components.PosterImage
import com.thomaskioko.tvmaniac.compose.components.RowError
import com.thomaskioko.tvmaniac.compose.components.TvShowCard
import com.thomaskioko.tvmaniac.compose.theme.contrastAgainst
import com.thomaskioko.tvmaniac.compose.theme.grey900
import com.thomaskioko.tvmaniac.compose.util.DominantColorState
import com.thomaskioko.tvmaniac.compose.util.DynamicThemePrimaryColorsFromImage
import com.thomaskioko.tvmaniac.compose.util.copy
import com.thomaskioko.tvmaniac.compose.util.rememberDominantColorState
import com.thomaskioko.tvmaniac.compose.util.verticalGradientScrim
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
import kotlin.math.absoluteValue

/**
 * This is the minimum amount of calculated contrast for a color to be used on top of the
 * surface color. These values are defined within the WCAG AA guidelines, and we use a value of
 * 3:1 which is the minimum for user-interface components.
 */
private const val MinContrastOfPrimaryVsSurface = 3f

@Composable
fun DiscoverScreen(
    viewModel: DiscoverViewModel,
    openShowDetails: (showId: Long) -> Unit,
    moreClicked: (showType: Long) -> Unit,
) {

    val scaffoldState = rememberScaffoldState()

    val discoverViewState by viewModel.state.collectAsStateWithLifecycle()

    DiscoverShows(
        scaffoldState = scaffoldState,
        showsState = discoverViewState,
        openShowDetails = openShowDetails,
        moreClicked = moreClicked,
        onRetry = { viewModel.dispatch(RetryLoading) },
        reloadCategory = { viewModel.dispatch(it) }
    )
}

@Composable
private fun DiscoverShows(
    scaffoldState: ScaffoldState,
    showsState: ShowsState,
    openShowDetails: (showId: Long) -> Unit,
    moreClicked: (showType: Long) -> Unit,
    reloadCategory: (ShowsAction) -> Unit,
    onRetry: () -> Unit,
) {
    Scaffold(
        scaffoldState = scaffoldState,
    ) { contentPadding ->

        when (showsState) {
            Loading -> FullScreenLoading()
            is ShowsLoaded -> {
                DiscoverViewScrollingContent(
                    contentPadding = contentPadding,
                    state = showsState,
                    openShowDetails = openShowDetails,
                    reloadCategory = reloadCategory,
                    moreClicked = moreClicked,
                )
            }

            is LoadingError -> ErrorUi(errorMessage = showsState.errorMessage, onRetry = onRetry)
        }
    }
}

@Composable
private fun DiscoverViewScrollingContent(
    contentPadding: PaddingValues,
    state: ShowsLoaded,
    openShowDetails: (showId: Long) -> Unit,
    reloadCategory: (ShowsAction) -> Unit,
    moreClicked: (showType: Long) -> Unit
) {
    LazyColumn(
        contentPadding = contentPadding.copy(copyTop = false),
        modifier = Modifier
            .imePadding()
            .navigationBarsPadding()
            .animateContentSize(),
    ) {

        item {
            when (state.result.featuredCategoryState) {
                is ShowResult.CategoryError -> {
                    CategoryError(
                        categoryTitle = Category.FEATURED.title,
                        onRetry = { reloadCategory(ReloadFeatured) }
                    )
                }

                is CategorySuccess -> {
                    val resultState =
                        (state.result.featuredCategoryState as CategorySuccess)
                    FeaturedItems(
                        showList = resultState.tvShows,
                        onItemClicked = openShowDetails
                    )
                }

                ShowResult.EmptyCategoryData ->
                    EmptyContentView(
                        painter = painterResource(id = R.drawable.ic_watchlist_empty),
                        message = stringResource(id = R.string.generic_empty_content)
                    )
            }

        }

        item {
            when (state.result.trendingCategoryState) {
                is ShowResult.CategoryError -> {
                    CategoryError(
                        categoryTitle = Category.TRENDING.title,
                        onRetry = { reloadCategory(ReloadFeatured) }
                    )
                }

                is CategorySuccess -> {
                    val resultState =
                        (state.result.trendingCategoryState as CategorySuccess)
                    DisplayShowData(
                        category = resultState.category,
                        tvShows = resultState.tvShows,
                        onItemClicked = openShowDetails,
                        moreClicked = moreClicked
                    )
                }

                ShowResult.EmptyCategoryData ->
                    EmptyContentView(
                        painter = painterResource(id = R.drawable.ic_watchlist_empty),
                        message = stringResource(id = R.string.generic_empty_content)
                    )
            }

        }

        item {
            when (state.result.anticipatedCategoryState) {
                is ShowResult.CategoryError -> {
                    CategoryError(
                        categoryTitle = Category.ANTICIPATED.title,
                        onRetry = { reloadCategory(ReloadFeatured) }
                    )
                }

                is CategorySuccess -> {
                    val resultState =
                        (state.result.anticipatedCategoryState as CategorySuccess)
                    DisplayShowData(
                        category = resultState.category,
                        tvShows = resultState.tvShows,
                        onItemClicked = openShowDetails,
                        moreClicked = moreClicked
                    )
                }

                ShowResult.EmptyCategoryData ->
                    EmptyContentView(
                        painter = painterResource(id = R.drawable.ic_watchlist_empty),
                        message = stringResource(id = R.string.generic_empty_content)
                    )
            }
        }

        item {
            when (state.result.popularCategoryState) {
                is ShowResult.CategoryError -> {
                    CategoryError(
                        categoryTitle = Category.POPULAR.title,
                        onRetry = { reloadCategory(ReloadFeatured) }
                    )
                }

                is CategorySuccess -> {
                    val resultState =
                        (state.result.popularCategoryState as CategorySuccess)
                    DisplayShowData(
                        category = resultState.category,
                        tvShows = resultState.tvShows,
                        onItemClicked = openShowDetails,
                        moreClicked = moreClicked
                    )
                }

                ShowResult.EmptyCategoryData ->
                    EmptyContentView(
                        painter = painterResource(id = R.drawable.ic_watchlist_empty),
                        message = stringResource(id = R.string.generic_empty_content)
                    )
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun FeaturedItems(
    showList: List<TvShow>,
    onItemClicked: (Long) -> Unit,
) {

    val surfaceColor = grey900
    val dominantColorState = rememberDominantColorState { color ->
        // We want a color which has sufficient contrast against the surface color
        color.contrastAgainst(surfaceColor) >= MinContrastOfPrimaryVsSurface
    }
    val pagerState = rememberPagerState()

    DynamicThemePrimaryColorsFromImage(dominantColorState) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalGradientScrim(
                    color = MaterialTheme.colors.primary.copy(alpha = 0.38f),
                    startYPercentage = 1f,
                    endYPercentage = 0f
                )
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            ColumnSpacer(value = 24)

            FeaturedHorizontalPager(
                list = showList,
                pagerState = pagerState,
                dominantColorState = dominantColorState,
                onClick = onItemClicked
            )

            if (showList.isNotEmpty())
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp),
                )
        }
    }

    ColumnSpacer(value = 16)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun FeaturedHorizontalPager(
    list: List<TvShow>,
    pagerState: PagerState,
    dominantColorState: DominantColorState,
    onClick: (Long) -> Unit
) {

    val selectedImageUrl = list.getOrNull(pagerState.currentPage)
        ?.posterImageUrl

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
        modifier = Modifier
            .fillMaxSize()
    ) { pageNumber ->

        PosterImage(
            title = list[pageNumber].title,
            posterImageUrl = list[pageNumber].posterImageUrl,
            modifier = Modifier
                .clickable { onClick(list[pageNumber].traktId) }
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
                .fillMaxWidth()
                .aspectRatio(0.7f),
            posterModifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .offset {
                    val pageOffset =
                        this@HorizontalPager.calculateCurrentOffsetForPage(pageNumber)
                    // Then use it as a multiplier to apply an offset
                    IntOffset(
                        x = (37.dp * pageOffset).roundToPx(),
                        y = 0
                    )
                }
        )
    }
}

@OptIn(ExperimentalSnapperApi::class)
@Composable
private fun DisplayShowData(
    category: Category,
    tvShows: List<TvShow>,
    onItemClicked: (Long) -> Unit,
    moreClicked: (Long) -> Unit,
) {

    AnimatedVisibility(visible = tvShows.isNotEmpty()) {
        Column {
            BoxTextItems(
                title = category.title,
                moreString = stringResource(id = R.string.str_more),
                onMoreClicked = { moreClicked(category.id) }
            )

            val lazyListState = rememberLazyListState()

            LazyRow(
                state = lazyListState,
                flingBehavior = rememberSnapperFlingBehavior(lazyListState),
            ) {
                itemsIndexed(tvShows) { index, tvShow ->
                    TvShowCard(
                        posterImageUrl = tvShow.posterImageUrl,
                        title = tvShow.title,
                        isFirstCard = index == 0,
                        onClick = { onItemClicked(tvShow.traktId) }
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
) {

    Column {
        BoxTextItems(
            title = categoryTitle
        )

        RowError(onRetry = { onRetry() })
    }
}

@Preview
@Composable
fun DiscoverScreenPreview() {
    Surface(Modifier.fillMaxWidth()) {
        DiscoverViewScrollingContent(
            contentPadding = PaddingValues(0.dp),
            state = showsLoaded,
            openShowDetails = {},
            moreClicked = {},
            reloadCategory = {}
        )
    }
}
