package com.thomaskioko.tvmaniac.show_grid

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import com.thomaskioko.tvmaniac.compose.R
import com.thomaskioko.tvmaniac.compose.components.BackAppBar
import com.thomaskioko.tvmaniac.compose.components.EmptyContentView
import com.thomaskioko.tvmaniac.compose.components.LazyGridItems
import com.thomaskioko.tvmaniac.compose.components.NetworkImageComposable
import com.thomaskioko.tvmaniac.compose.rememberFlowWithLifecycle
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.presentation.contract.ShowsGridState

@Composable
fun ShowsGridScreen(
    viewModel: ShowGridViewModel,
    openShowDetails: (showId: Int) -> Unit,
    navigateUp: () -> Unit
) {

    val gridViewState by rememberFlowWithLifecycle(viewModel.observeState())
        .collectAsState(initial = ShowsGridState.Empty)

    Scaffold(
        topBar = {
            BackAppBar(
                title = ShowCategory[viewModel.showType].title,
                onBackClick = navigateUp
            )
        },
        modifier = Modifier
            .statusBarsPadding(),
    ) {

        ShowsGridContent(
            viewState = gridViewState,
            onItemClicked = { openShowDetails(it) }
        )
    }
}

@Composable
fun ShowsGridContent(
    viewState: ShowsGridState,
    onItemClicked: (Int) -> Unit,
) {

    val listState = rememberLazyListState()
    if (viewState.list.isEmpty())
        EmptyContentView(
            painter = painterResource(id = R.drawable.ic_watchlist_empty),
            message = stringResource(id = R.string.msg_empty_category)
        )
    else
        LazyGridItems(
            listState = listState,
            items = viewState.list
        ) { show ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Card(
                    elevation = 4.dp,
                    modifier = Modifier.clickable { onItemClicked(show.id) },
                    shape = MaterialTheme.shapes.medium
                ) {
                    NetworkImageComposable(
                        imageUrl = show.posterImageUrl,
                        contentDescription = stringResource(
                            R.string.cd_show_poster,
                            show.title
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(2 / 3f)
                    )
                }
            }
        }
}