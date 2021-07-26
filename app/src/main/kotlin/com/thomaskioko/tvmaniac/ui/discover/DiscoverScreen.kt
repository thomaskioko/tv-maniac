package com.thomaskioko.tvmaniac.ui.discover

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.rememberPagerState
import com.thomaskioko.tvmaniac.compose.components.BoxTextItems
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.components.ErrorView
import com.thomaskioko.tvmaniac.compose.components.HorizontalPager
import com.thomaskioko.tvmaniac.compose.components.LoadingView
import com.thomaskioko.tvmaniac.compose.components.TvManiacScaffold
import com.thomaskioko.tvmaniac.compose.components.TvShowCard
import com.thomaskioko.tvmaniac.compose.theme.contrastAgainst
import com.thomaskioko.tvmaniac.compose.util.DynamicThemePrimaryColorsFromImage
import com.thomaskioko.tvmaniac.compose.util.rememberDominantColorState
import com.thomaskioko.tvmaniac.compose.util.verticalGradientScrim
import com.thomaskioko.tvmaniac.core.rememberFlowWithLifecycle
import com.thomaskioko.tvmaniac.datasource.enums.TrendingDataRequest
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import io.github.aakira.napier.Napier

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

    val discoverViewState by rememberFlowWithLifecycle(viewModel.stateFlow)
        .collectAsState(initial = DiscoverShowsState.Loading)

    TvManiacScaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier
            .statusBarsPadding(),
        content = {
            LoadScreenContent(
                viewState = discoverViewState,
                onItemClicked = { tvShowId ->
                    openShowDetails(tvShowId)
                }
            )
        }
    )

}

@Composable
fun LoadScreenContent(
    viewState: DiscoverShowsState,
    onItemClicked: (Int) -> Unit,
) {

    when (viewState) {
        is DiscoverShowsState.Error -> ErrorView(viewState.message)
        DiscoverShowsState.Loading -> LoadingView()
        is DiscoverShowsState.Success -> ScreenData(viewState, onItemClicked)
    }

}

@Composable
private fun ScreenData(
    viewState: DiscoverShowsState.Success,
    onItemClicked: (Int) -> Unit,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 54.dp)
    ) {
        item {
            viewState.dataMap.forEach {
                if (it.key.title == "Featured") {
                    FeaturedItems(it, onItemClicked)
                } else {
                    DisplayShowData(it, onItemClicked)
                }
            }
        }
    }
}

@Composable
fun FeaturedItems(
    resultMap: Map.Entry<TrendingDataRequest, List<TvShow>>,
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalGradientScrim(
                    color = MaterialTheme.colors.primary.copy(alpha = 0.38f),
                    startYPercentage = 1f,
                    endYPercentage = 0f
                )
        ) {

            ColumnSpacer(value = 16)

            BoxTextItems(resultMap.key.title)

            HorizontalPager(resultMap.value, pagerState) { tvShowId ->
                onItemClicked(tvShowId)
            }
        }
    }

    ColumnSpacer(value = 8)
}

@Composable
private fun DisplayShowData(
    resultMap: Map.Entry<TrendingDataRequest, List<TvShow>>,
    onItemClicked: (Int) -> Unit,
) {

    ColumnSpacer(value = 8)

    BoxTextItems(resultMap.key.title, "More") {
        Napier.d("${resultMap.key.type} Clicked")
    }


    LazyRow {
        itemsIndexed(resultMap.value) { index, tvShow ->
            TvShowCard(tvShow = tvShow, isFirstCard = index == 0) {
                onItemClicked(tvShow.id)
            }
        }
    }
}

