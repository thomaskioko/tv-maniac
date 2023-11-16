package com.thomaskioko.tvmaniac.showsgrid

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
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thomaskioko.tvmaniac.category.api.model.getCategory
import com.thomaskioko.tvmaniac.common.localization.MR
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.navigation.extensions.viewModel
import com.thomaskioko.tvmaniac.showsgrid.model.TvShow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias ShowsGrid = @Composable (
    onBackClicked: () -> Unit,
    openShowDetails: (showId: Long) -> Unit,
) -> Unit

@Inject
@Composable
fun ShowsGrid(
    viewModelFactory: (SavedStateHandle) -> ShowGridViewModel,
    @Assisted onBackClicked: () -> Unit,
    @Assisted openShowDetails: (showId: Long) -> Unit,
) {
    GridScreen(
        viewModel = viewModel(factory = viewModelFactory),
        openShowDetails = openShowDetails,
        onBackClicked = onBackClicked,
    )
}

@Composable
internal fun GridScreen(
    openShowDetails: (showId: Long) -> Unit,
    onBackClicked: () -> Unit,
    viewModel: ShowGridViewModel,
    modifier: Modifier = Modifier,
) {
    val gridViewState by viewModel.state.collectAsStateWithLifecycle()

    GridScreen(
        onBackClicked = onBackClicked,
        state = gridViewState,
        modifier = modifier,
        title = viewModel.showType.getCategory().title, // TODO:: Remove this and do the mapping from the state machine
        onRetry = { viewModel.dispatch(ReloadShows(viewModel.showType)) },
        onShowClicked = { openShowDetails(it) },
    )
}

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
private fun GridScreen(
    onBackClicked: () -> Unit,
    state: GridState,
    title: String,
    onShowClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TvManiacTopBar(
                title = title,
                onBackClick = onBackClicked,
            )
        },
        modifier = Modifier,
    ) { contentPadding ->
        when (state) {
            LoadingContent -> LoadingIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
            )

            is LoadingContentError -> ErrorUi(
                errorMessage = state.errorMessage,
                onRetry = onRetry,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
            )

            is ShowsLoaded -> GridContent(
                modifier = modifier
                    .fillMaxSize(),
                contentPadding = contentPadding,
                list = state.list,
                onItemClicked = onShowClicked,
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
    modifier: Modifier = Modifier,
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
                    .padding(horizontal = 2.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1F)
                        .align(Alignment.Top)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(),
                    ) {
                        Card(
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 4.dp,
                            ),
                            modifier = Modifier.clickable { onItemClicked(show.traktId) },
                        ) {
                            AsyncImageComposable(
                                model = show.posterImageUrl,
                                contentDescription = stringResource(
                                    MR.strings.cd_show_poster.resourceId,
                                    show.title,
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

@ThemePreviews
@Composable
private fun ShowsGridContentPreview(
    @PreviewParameter(GridPreviewParameterProvider::class)
    state: GridState,
) {
    TvManiacTheme {
        Surface {
            GridScreen(
                state = state,
                title = "Anticipated",
                onShowClicked = {},
                onBackClicked = {},
                onRetry = {},
            )
        }
    }
}
