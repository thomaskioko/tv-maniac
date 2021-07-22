package com.thomaskioko.tvmaniac.ui.discover

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.insets.statusBarsPadding
import com.thomaskioko.tvmaniac.compose.components.BoxTextItems
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.components.ErrorView
import com.thomaskioko.tvmaniac.compose.components.HorizontalPager
import com.thomaskioko.tvmaniac.compose.components.LoadingView
import com.thomaskioko.tvmaniac.compose.components.TvManiacScaffold
import com.thomaskioko.tvmaniac.compose.components.TvShowCard
import com.thomaskioko.tvmaniac.datasource.cache.model.TvShow
import com.thomaskioko.tvmaniac.datasource.enums.TrendingDataRequest
import com.thomaskioko.tvmaniac.navigation.NavigationScreen
import io.github.aakira.napier.Napier

@Composable
fun DiscoverScreen(
    viewModel: DiscoverViewModel,
    navController: NavHostController,
) {

    val scaffoldState = rememberScaffoldState()

    val lifecycleOwner = LocalLifecycleOwner.current
    val actionState = viewModel.stateFlow

    val actionStateLifeCycleAware = remember(actionState, lifecycleOwner) {
        actionState.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
    }

    val discoverViewState by actionStateLifeCycleAware
        .collectAsState(initial = DiscoverShowsState.Loading)

    TvManiacScaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier
            .statusBarsPadding(),
        content = {
            LoadScreenContent(
                viewState = discoverViewState,
                onItemClicked = { tvShowId ->
                    navController.navigate("${NavigationScreen.ShowDetailsNavScreen.route}/$tvShowId")
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
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
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

    Column {

        ColumnSpacer(value = 8)

        BoxTextItems(resultMap.key.title)

        HorizontalPager(resultMap.value) { tvShowId ->
            Napier.d("Show Clicked $tvShowId")
            onItemClicked(tvShowId)
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
                Napier.d("${tvShow.title} Clicked")
                onItemClicked(tvShow.id)
            }
        }

    }
}

