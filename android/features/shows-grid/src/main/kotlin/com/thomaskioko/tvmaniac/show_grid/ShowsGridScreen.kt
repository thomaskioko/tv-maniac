package com.thomaskioko.tvmaniac.show_grid

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thomaskioko.tvmaniac.category.api.model.Category
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.CircularLoadingView
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.show_grid.model.TvShow

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ShowsGridScreen(
    viewModel: ShowGridViewModel,
    openShowDetails: (showId: Long) -> Unit,
    navigateUp: () -> Unit
) {

    val gridViewState by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TvManiacTopBar(
                title = Category[viewModel.showType].title, //TODO:: Remove this and do the mapping from the state machine
                onBackClick = navigateUp
            )
        },
        modifier = Modifier
            .statusBarsPadding(),
    ) { contentPadding ->
        when (gridViewState) {
            LoadingContent -> CircularLoadingView()
            is LoadingContentError -> ErrorUi(
                errorMessage = (gridViewState as LoadingContentError).errorMessage,
                onRetry = { viewModel.dispatch(ReloadShows(viewModel.showType)) })

            is ShowsLoaded -> GridContent(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = contentPadding,
                list = (gridViewState as ShowsLoaded).list,
                onItemClicked = { openShowDetails(it) }
            )
        }


    }
}

@ExperimentalFoundationApi
@Composable
fun GridContent(
    list: List<TvShow>,
    contentPadding: PaddingValues,
    onItemClicked: (Long) -> Unit,
    modifier: Modifier = Modifier
) {

    val listState = rememberLazyGridState()

    LazyVerticalGrid(
        modifier = modifier,
        state = listState,
        columns = GridCells.Fixed(3),
        contentPadding = contentPadding.copy(copyTop = false),
    ) {

        items(list) { show ->

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
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 4.dp
                            ),
                            modifier = Modifier.clickable { onItemClicked(show.traktId) },
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

@OptIn(ExperimentalFoundationApi::class)
@ThemePreviews
@Composable
fun ShowsGridContentPreview() {
    TvManiacTheme {
        Surface {
            GridContent(
                list = showList,
                contentPadding = PaddingValues(),
                onItemClicked = {}
            )
        }
    }
}
