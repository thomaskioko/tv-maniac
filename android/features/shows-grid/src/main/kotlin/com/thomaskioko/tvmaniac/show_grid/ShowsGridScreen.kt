package com.thomaskioko.tvmaniac.show_grid

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.thomaskioko.tvmaniac.compose.components.BackAppBar
import com.thomaskioko.tvmaniac.compose.components.NetworkImageComposable
import com.thomaskioko.tvmaniac.compose.rememberFlowWithLifecycle
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.showcommon.api.model.ShowCategory

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowsGridScreen(
    viewModel: ShowGridViewModel,
    openShowDetails: (showId: Int) -> Unit,
    navigateUp: () -> Unit
) {

    val scaffoldState = rememberScaffoldState()

    val gridViewState by rememberFlowWithLifecycle(viewModel.observeState())
        .collectAsState(initial = ShowsLoaded.Empty)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            BackAppBar(
                title = ShowCategory[viewModel.showType].title,
                onBackClick = navigateUp
            )
        },
        modifier = Modifier
            .statusBarsPadding(),
    ) { paddingValues ->

        ShowsGridContent(
            hostState = scaffoldState.snackbarHostState,
            viewState = gridViewState,
            paddingValues = paddingValues,
            onItemClicked = { openShowDetails(it) }
        )
    }
}

@ExperimentalFoundationApi
@Composable
fun ShowsGridContent(
    hostState: SnackbarHostState,
    viewState: ShowsLoaded,
    paddingValues: PaddingValues,
    onItemClicked: (Int) -> Unit,
) {

    val listState = rememberLazyGridState()
    val lazyShowList = viewState.list.collectAsLazyPagingItems()

    LazyPagedGridItems(
        listState = listState,
        lazyPagingItems = lazyShowList,
        hostState = hostState,
    ) { show ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            show?.let {
                Card(
                    elevation = 4.dp,
                    modifier = Modifier.clickable { onItemClicked(show.traktId) },
                    shape = MaterialTheme.shapes.medium
                ) {
                    NetworkImageComposable(
                        imageUrl = show.posterImageUrl,
                        contentDescription = stringResource(
                            R.string.cd_show_poster,
                            show.title
                        ),
                        modifier = Modifier
                            .weight(1F)
                            .aspectRatio(2 / 3f),
                    )
                }
            }
        }
    }
}
