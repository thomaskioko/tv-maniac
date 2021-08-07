package com.thomaskioko.tvmaniac

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
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
import com.thomaskioko.tvmaniac.compose.util.DynamicThemePrimaryColorsFromImage
import com.thomaskioko.tvmaniac.compose.util.rememberDominantColorState
import com.thomaskioko.tvmaniac.compose.util.verticalGradientScrim
import com.thomaskioko.tvmaniac.core.discover.DiscoverShowEffect
import com.thomaskioko.tvmaniac.core.discover.DiscoverShowState
import com.thomaskioko.tvmaniac.datasource.enums.TvShowType
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
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
        content = {
            ScreenData(
                viewState = discoverViewState,
                onItemClicked = { tvShowId ->
                    openShowDetails(tvShowId)
                }
            )
        }
    )

}

@Composable
private fun ScreenData(
    viewState: DiscoverShowState,
    onItemClicked: (Int) -> Unit,
) {
    if (viewState.isLoading) LoadingView()

    val listState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
            .padding(bottom = 54.dp)
            .animateContentSize()
    ) {

        viewState.dataMap.forEach {
            if (it.key.title == "Featured") {
                FeaturedItems(it, onItemClicked)
            } else {
                DisplayShowData(it, onItemClicked)
            }
        }

    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun FeaturedItems(
    resultMap: Map.Entry<TvShowType, List<TvShow>>,
    onItemClicked: (Int) -> Unit,
) {

    val surfaceColor = MaterialTheme.colors.surface
    val dominantColorState = rememberDominantColorState { color ->
        // We want a color which has sufficient contrast against the surface color
        color.contrastAgainst(surfaceColor) >= MinContrastOfPrimaryVsSurface
    }

    DynamicThemePrimaryColorsFromImage(dominantColorState) {

        val pagerState = rememberPagerState(
            pageCount = resultMap.value.size,
            initialOffscreenLimit = 2,
        )

        val selectedImageUrl = resultMap.value.getOrNull(pagerState.currentPage)
            ?.posterImageUrl

        LaunchedEffect(selectedImageUrl) {
            if (selectedImageUrl != null) {
                dominantColorState.updateColorsFromImageUrl(selectedImageUrl)
            } else {
                dominantColorState.reset()
            }
        }

        LaunchedEffect(true) {
            repeat(Int.MAX_VALUE) {
                delay(1500)
                pagerState.animateScrollToPage(
                    page = it % pagerState.pageCount,
                    animationSpec = tween(2000)
                )
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

            FeaturedHorizontalPager(resultMap.value, pagerState) { tvShowId ->
                onItemClicked(tvShowId)
            }

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
    onClick: (Int) -> Unit
) {

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

        ) {
            Box {
                NetworkImageComposable(
                    imageUrl = list[pageNumber].posterImageUrl,
                    contentDescription = stringResource(
                        R.string.cd_show_poster,
                        list[pageNumber].title
                    ),
                    modifier = Modifier
                        .height(450.dp)
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium)
                )
            }
        }
    }
}

@Composable
private fun DisplayShowData(
    resultMap: Map.Entry<TvShowType, List<TvShow>>,
    onItemClicked: (Int) -> Unit,
) {

    ColumnSpacer(value = 8)

    BoxTextItems(resultMap.key.title, "More") {
        Napier.d("${resultMap.key.type} Clicked")
    }


    LazyRow {
        itemsIndexed(resultMap.value) { index, tvShow ->
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

