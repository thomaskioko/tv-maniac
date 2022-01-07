package com.thomaskioko.tvmaniac

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHost
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.thomaskioko.tvmaniac.compose.R
import com.thomaskioko.tvmaniac.compose.components.BoxTextItems
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.components.LoadingView
import com.thomaskioko.tvmaniac.compose.components.NetworkImageComposable
import com.thomaskioko.tvmaniac.compose.components.SwipeDismissSnackbar
import com.thomaskioko.tvmaniac.compose.components.TvShowCard
import com.thomaskioko.tvmaniac.compose.rememberFlowWithLifecycle
import com.thomaskioko.tvmaniac.compose.theme.contrastAgainst
import com.thomaskioko.tvmaniac.compose.theme.grey900
import com.thomaskioko.tvmaniac.compose.util.DynamicThemePrimaryColorsFromImage
import com.thomaskioko.tvmaniac.compose.util.rememberDominantColorState
import com.thomaskioko.tvmaniac.compose.util.verticalGradientScrim
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.presentation.contract.DiscoverShowEffect
import com.thomaskioko.tvmaniac.presentation.contract.DiscoverShowResult
import com.thomaskioko.tvmaniac.presentation.contract.DiscoverShowState
import com.thomaskioko.tvmaniac.presentation.model.ShowUiModel
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlin.math.absoluteValue

/**
 * This is the minimum amount of calculated contrast for a color to be used on top of the
 * surface color. These values are defined within the WCAG AA guidelines, and we use a value of
 * 3:1 which is the minimum for user-interface components.
 */
private const val MinContrastOfPrimaryVsSurface = 3f

@OptIn(ExperimentalSnapperApi::class, kotlinx.coroutines.InternalCoroutinesApi::class)
@Composable
fun DiscoverScreen(
    viewModel: DiscoverViewModel,
    openShowDetails: (showId: Int) -> Unit,
    moreClicked: (showType: Int) -> Unit,
) {

    val scaffoldState = rememberScaffoldState()

    val discoverViewState by rememberFlowWithLifecycle(viewModel.observeState())
        .collectAsState(initial = DiscoverShowState.Empty)

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

@OptIn(ExperimentalSnapperApi::class)
@Composable
private fun DiscoverShows(
    scaffoldState: ScaffoldState,
    discoverViewState: DiscoverShowState,
    openShowDetails: (showId: Int) -> Unit,
    moreClicked: (showType: Int) -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier
            .statusBarsPadding(),
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
                    .padding(bottom = 64.dp)
                    .fillMaxWidth()
            )
        },
    ) {
        if (discoverViewState.isLoading) LoadingView()

        LazyColumn(
            modifier = Modifier
                .navigationBarsWithImePadding()
                .animateContentSize(),
        ) {

            item {
                FeaturedItems(
                    showData = discoverViewState.showData.featuredShows
                ) { openShowDetails(it) }
            }

            item {
                DisplayShowData(
                    category = discoverViewState.showData.trendingShows.category,
                    showUiModels = discoverViewState.showData.trendingShows.showUiModels,
                    onItemClicked = { openShowDetails(it) },
                    moreClicked = { moreClicked(it) }
                )
            }

            item {
                DisplayShowData(
                    category = discoverViewState.showData.popularShows.category,
                    showUiModels = discoverViewState.showData.popularShows.showUiModels,
                    onItemClicked = { openShowDetails(it) },
                    moreClicked = { moreClicked(it) }
                )
            }

            item {
                DisplayShowData(
                    category = discoverViewState.showData.topRatedShows.category,
                    showUiModels = discoverViewState.showData.topRatedShows.showUiModels,
                    onItemClicked = { openShowDetails(it) },
                    moreClicked = { moreClicked(it) }
                )
            }
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

    DynamicThemePrimaryColorsFromImage(dominantColorState) {

        val pagerState = rememberPagerState()

        val selectedImageUrl = showData.showUiModels.getOrNull(pagerState.currentPage)
            ?.posterImageUrl

        LaunchedEffect(selectedImageUrl) {
            if (selectedImageUrl != null) {
                dominantColorState.updateColorsFromImageUrl(selectedImageUrl)
            } else {
                dominantColorState.reset()
            }
        }

        LaunchedEffect(showData.showUiModels) {
            if (showData.showUiModels.size >= 4) {
                pagerState.scrollToPage(2)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalGradientScrim(
                    color = MaterialTheme.colors.primary.copy(alpha = 0.38f),
                    startYPercentage = 1f,
                    endYPercentage = 0f
                )
        ) {

            ColumnSpacer(value = 24)

            FeaturedHorizontalPager(
                list = showData.showUiModels,
                pagerState = pagerState,
                onClick = { onItemClicked(it) }
            )

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
    list: List<ShowUiModel>,
    pagerState: PagerState,
    onClick: (Int) -> Unit
) {

    HorizontalPager(
        count = list.size,
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 45.dp),
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
                .fillMaxWidth()
                .aspectRatio(0.7f)

        ) {
            Box {
                NetworkImageComposable(
                    imageUrl = list[pageNumber].posterImageUrl,
                    contentDescription = stringResource(
                        R.string.cd_show_poster,
                        list[pageNumber].title
                    ),
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .offset {
                            val pageOffset =
                                this@HorizontalPager.calculateCurrentOffsetForPage(pageNumber)
                            // Then use it as a multiplier to apply an offset
                            IntOffset(
                                x = (36.dp * pageOffset).roundToPx(),
                                y = 0
                            )
                        }
                )
            }
        }
    }
}

@OptIn(ExperimentalSnapperApi::class)
@Composable
private fun DisplayShowData(
    category: ShowCategory,
    showUiModels: List<ShowUiModel>,
    onItemClicked: (Int) -> Unit,
    moreClicked: (Int) -> Unit,
) {

    AnimatedVisibility(visible = showUiModels.isNotEmpty()) {
        Column {
            BoxTextItems(
                title = category.title,
                moreString = stringResource(id = R.string.str_more),
                onMoreClicked = { moreClicked(category.type) }
            )

            val lazyListState = rememberLazyListState()

            LazyRow(
                state = lazyListState,
                flingBehavior = rememberSnapperFlingBehavior(lazyListState),
            ) {
                itemsIndexed(showUiModels) { index, tvShow ->
                    TvShowCard(
                        posterImageUrl = tvShow.posterImageUrl,
                        title = tvShow.title,
                        isFirstCard = index == 0
                    ) {
                        onItemClicked(tvShow.id)
                    }
                }
            }
        }
    }
}
