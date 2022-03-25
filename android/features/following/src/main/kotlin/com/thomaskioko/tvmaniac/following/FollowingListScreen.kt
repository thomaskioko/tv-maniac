package com.thomaskioko.tvmaniac.following

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import com.thomaskioko.tvmaniac.compose.components.EmptyContentView
import com.thomaskioko.tvmaniac.compose.components.LazyGridItems
import com.thomaskioko.tvmaniac.compose.components.NetworkImageComposable
import com.thomaskioko.tvmaniac.compose.components.SwipeDismissSnackbar
import com.thomaskioko.tvmaniac.compose.rememberFlowWithLifecycle
import com.thomaskioko.tvmaniac.resources.R
import kotlinx.coroutines.InternalCoroutinesApi

@OptIn(InternalCoroutinesApi::class)
@Composable
fun FollowingContent(
    viewModel: FollowingViewModel,
    openShowDetails: (showId: Long) -> Unit,
) {

    val watchlistViewState by rememberFlowWithLifecycle(viewModel.observeState())
        .collectAsState(initial = WatchlistState.Empty)

    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(Unit) {
        viewModel.observeSideEffect().collect {
            when (it) {
                is WatchlistEffect.Error -> scaffoldState.snackbarHostState.showSnackbar(it.message)
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier
            .statusBarsPadding()
            .padding(bottom = 64.dp),
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
            WatchlistContent(
                viewState = watchlistViewState,
                onItemClicked = { tvShowId ->
                    openShowDetails(tvShowId)
                }
            )
        }
    )
}

@Composable
private fun WatchlistContent(
    viewState: WatchlistState,
    onItemClicked: (Long) -> Unit,
) {
    val listState = rememberLazyListState()

    if (viewState.list.isEmpty())
        EmptyContentView(
            painter = painterResource(id = R.drawable.ic_watchlist_empty),
            message = stringResource(id = R.string.msg_empty_favorites)
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
