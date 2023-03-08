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
import androidx.compose.material.SnackbarHost
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
import com.thomaskioko.tvmaniac.compose.components.BoxTextItems
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.components.EmptyContentView
import com.thomaskioko.tvmaniac.compose.components.FullScreenLoading
import com.thomaskioko.tvmaniac.compose.components.PosterImage
import com.thomaskioko.tvmaniac.compose.components.SwipeDismissSnackbar
import com.thomaskioko.tvmaniac.compose.components.TvShowCard
import com.thomaskioko.tvmaniac.compose.theme.contrastAgainst
import com.thomaskioko.tvmaniac.compose.theme.grey900
import com.thomaskioko.tvmaniac.compose.util.DominantColorState
import com.thomaskioko.tvmaniac.compose.util.DynamicThemePrimaryColorsFromImage
import com.thomaskioko.tvmaniac.compose.util.copy
import com.thomaskioko.tvmaniac.compose.util.rememberDominantColorState
import com.thomaskioko.tvmaniac.compose.util.verticalGradientScrim
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.shows.api.DiscoverShowEffect
import com.thomaskioko.tvmaniac.shows.api.DiscoverShowResult
import com.thomaskioko.tvmaniac.shows.api.DiscoverShowState
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory
import com.thomaskioko.tvmaniac.shows.api.model.TvShow
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
    openShowDetails: (showId: Int) -> Unit,
    moreClicked: (showType: Int) -> Unit,
) {

    val scaffoldState = rememberScaffoldState()

    val discoverViewState by viewModel.observeState().collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.observeSideEffect().collect {
            when (it) {
                is DiscoverShowEffect.Error -> scaffoldState.snackbarHostState.showSnackbar(it.message)
            }
        }
    }

    DiscoverShows(
        scaffoldState = scaffoldState,
        discoverViewState = discoverViewState,
        openShowDetails = openShowDetails,
        moreClicked = moreClicked
    )
}

@Composable
private fun DiscoverShows(
    scaffoldState: ScaffoldState,
    discoverViewState: DiscoverShowState,
    openShowDetails: (showId: Int) -> Unit,
    moreClicked: (showType: Int) -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { snackBarHostState ->
            SnackbarHost(
                hostState = snackBarHostState,
                snackbar = { snackBarData ->
                    SwipeDismissSnackbar(
                        data = snackBarData,
                        onDismiss = { }
                    )
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
            )
        },
    ) { contentPadding ->

        //TODO:: Improve Ui state.
        when {
            discoverViewState.isLoading -> FullScreenLoading()
            discoverViewState.isEmpty ->
                EmptyContentView(
                    painter = painterResource(id = R.drawable.ic_watchlist_empty),
                    message = stringResource(id = R.string.generic_error_message)
                )
            else -> DiscoverViewScrollingContent(
                contentPadding,
                discoverViewState,
                openShowDetails,
                moreClicked
            )
        }
    }
}

@Composable
private fun DiscoverViewScrollingContent(
    contentPadding: PaddingValues,
    discoverViewState: DiscoverShowState,
    openShowDetails: (showId: Int) -> Unit,
    moreClicked: (showType: Int) -> Unit
) {
    LazyColumn(
        contentPadding = contentPadding.copy(copyTop = false),
        modifier = Modifier
            .imePadding()
            .navigationBarsPadding()
            .animateContentSize(),
    ) {

        item {
            FeaturedItems(
                showData = discoverViewState.featuredShows,
                onItemClicked = openShowDetails
            )
        }

        item {
            DisplayShowData(
                category = discoverViewState.trendingShows.category,
                tvShows = discoverViewState.trendingShows.tvShows,
                onItemClicked = openShowDetails,
                moreClicked = moreClicked
            )
        }

        item {
            DisplayShowData(
                category = discoverViewState.recommendedShows.category,
                tvShows = discoverViewState.recommendedShows.tvShows,
                onItemClicked = openShowDetails,
                moreClicked = moreClicked
            )
        }

        item {
            DisplayShowData(
                category = discoverViewState.anticipatedShows.category,
                tvShows = discoverViewState.anticipatedShows.tvShows,
                onItemClicked = openShowDetails,
                moreClicked = moreClicked
            )
        }

        item {
            DisplayShowData(
                category = discoverViewState.popularShows.category,
                tvShows = discoverViewState.popularShows.tvShows,
                onItemClicked = openShowDetails,
                moreClicked = moreClicked
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun FeaturedItems(
    showData: DiscoverShowResult.DiscoverShowsData,
    onItemClicked: (Int) -> Unit,
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
                list = showData.tvShows,
                pagerState = pagerState,
                dominantColorState = dominantColorState,
                onClick = onItemClicked
            )

            if (showData.tvShows.isNotEmpty())
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
    onClick: (Int) -> Unit
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
    category: ShowCategory,
    tvShows: List<TvShow>,
    onItemClicked: (Int) -> Unit,
    moreClicked: (Int) -> Unit,
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

@Preview
@Composable
fun DiscoverScreenPreview() {
    Surface(Modifier.fillMaxWidth()) {
        DiscoverViewScrollingContent(
            contentPadding = PaddingValues(0.dp),
            discoverViewState = discoverStatePreview,
            openShowDetails = {},
            moreClicked = {}
        )
    }
}
