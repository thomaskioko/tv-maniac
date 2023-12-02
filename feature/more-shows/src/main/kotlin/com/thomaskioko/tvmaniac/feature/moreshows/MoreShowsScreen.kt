package com.thomaskioko.tvmaniac.feature.moreshows

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.presentation.moreshows.BackClicked
import com.thomaskioko.tvmaniac.presentation.moreshows.MoreShowsActions
import com.thomaskioko.tvmaniac.presentation.moreshows.MoreShowsPresenter
import com.thomaskioko.tvmaniac.presentation.moreshows.MoreShowsState
import com.thomaskioko.tvmaniac.presentation.moreshows.ShowClicked
import com.thomaskioko.tvmaniac.presentation.moreshows.TvShow
import com.thomaskioko.tvmaniac.resources.R
import kotlinx.collections.immutable.ImmutableList

@Composable
fun MoreShowsScreen(
    presenter: MoreShowsPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsState()

    MoreShowsScreen(
        modifier = modifier,
        state = state,
        onAction = presenter::dispatch,
    )
}

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
internal fun MoreShowsScreen(
    state: MoreShowsState,
    onAction: (MoreShowsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TvManiacTopBar(
                title = state.categoryTitle,
                onBackClick = { onAction(BackClicked) },
            )
        },
        modifier = Modifier,
    ) { contentPadding ->

        GridContent(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentPadding = contentPadding,
            list = state.list,
            onItemClicked = { onAction(ShowClicked(it)) },
        )
    }
}

@ExperimentalFoundationApi
@Composable
fun GridContent(
    list: ImmutableList<TvShow>,
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
                                    R.string.cd_show_poster,
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
    @PreviewParameter(MoreShowsPreviewParameterProvider::class)
    state: MoreShowsState,
) {
    TvManiacTheme {
        Surface {
            MoreShowsScreen(
                state = state,
                onAction = {},
            )
        }
    }
}
