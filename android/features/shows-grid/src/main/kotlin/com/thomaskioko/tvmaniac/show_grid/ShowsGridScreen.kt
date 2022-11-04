package com.thomaskioko.tvmaniac.show_grid

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.BackAppBar
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowsGridScreen(
    viewModel: ShowGridViewModel,
    openShowDetails: (showId: Int) -> Unit,
    navigateUp: () -> Unit
) {

    val scaffoldState = rememberScaffoldState()

    val gridViewState by viewModel.observeState().collectAsStateWithLifecycle()

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
            viewState = gridViewState,
            paddingValues = paddingValues,
            onItemClicked = { openShowDetails(it) }
        )
    }
}

@ExperimentalFoundationApi
@Composable
fun ShowsGridContent(
    viewState: ShowsLoaded,
    paddingValues: PaddingValues,
    onItemClicked: (Int) -> Unit,
) {

    val listState = rememberLazyGridState()

    LazyVerticalGrid(
        state = listState,
        columns = GridCells.Fixed(3),
    ) {

        items(viewState.list) { show ->

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(horizontal = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1F)
                        .align(Alignment.Top)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                    ) {
                        Card(
                            elevation = 4.dp,
                            modifier = Modifier.clickable { onItemClicked(show.traktId) },
                            shape = MaterialTheme.shapes.medium
                        ) {
                            AsyncImageComposable(
                                model = show.posterImageUrl,
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
    }
}
