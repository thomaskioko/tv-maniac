package com.thomaskioko.tvmaniac.ui.discover

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import com.thomaskioko.tvmaniac.R
import com.thomaskioko.tvmaniac.compose.components.BoxTextItems
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.components.HorizontalPager
import com.thomaskioko.tvmaniac.compose.components.TvManiacScaffold
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.components.TvShowCard
import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowsEntity
import com.thomaskioko.tvmaniac.datasource.enums.TrendingDataRequest
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
        appBar = {
            TvManiacTopBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        color = MaterialTheme.colors.secondary
                    )
                },
            )
        },
        content = { innerPadding ->

            LoadScreenContent(
                viewState = discoverViewState,
                innerPadding = innerPadding
            )
        }
    )

}

@Composable
fun LoadScreenContent(
    viewState: DiscoverShowsState,
    innerPadding: PaddingValues
) {

    when (viewState) {
        is DiscoverShowsState.Error -> {
        }
        DiscoverShowsState.Loading -> {
        }
        is DiscoverShowsState.Success -> ScreenData(viewState)
    }

}

@Composable
private fun ScreenData(viewState: DiscoverShowsState.Success) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            viewState.dataMap.forEach {
                if (it.key.title == "Featured") {
                    FeaturedItems(it)
                } else {
                    DisplayShowData(it)
                }
            }
        }
    }
}

@Composable
fun FeaturedItems(
    resultMap: Map.Entry<TrendingDataRequest, List<TvShowsEntity>>,
) {

    ColumnSpacer(value = 8)

    BoxTextItems(resultMap.key.title)

    Column {
        HorizontalPager(resultMap.value) { tvShowId ->
            Napier.d("Show Clicked $tvShowId")
        }
    }

    ColumnSpacer(value = 8)
}

@Composable
private fun DisplayShowData(
    resultMap: Map.Entry<TrendingDataRequest, List<TvShowsEntity>>,
) {

    ColumnSpacer(value = 8)

    BoxTextItems(resultMap.key.title, "More") {
        Napier.d("${resultMap.key.type} Clicked")
    }


    LazyRow {
        itemsIndexed(resultMap.value) { index, tvShow ->
            TvShowCard(entity = tvShow, isFirstCard = index == 0) {
                Napier.d("${tvShow.title} Clicked")
            }
        }

    }
}

