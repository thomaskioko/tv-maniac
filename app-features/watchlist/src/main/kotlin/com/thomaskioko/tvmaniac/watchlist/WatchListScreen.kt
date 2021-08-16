package com.thomaskioko.tvmaniac.watchlist

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.statusBarsPadding
import com.thomaskioko.tvmaniac.compose.R
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.components.LazyGridItems
import com.thomaskioko.tvmaniac.compose.components.NetworkImageComposable
import com.thomaskioko.tvmaniac.compose.components.SwipeDismissSnackbar
import com.thomaskioko.tvmaniac.compose.rememberFlowWithLifecycle
import com.thomaskioko.tvmaniac.presentation.contract.WatchlistEffect
import com.thomaskioko.tvmaniac.presentation.contract.WatchlistState
import kotlinx.coroutines.flow.collect

@Composable
fun WatchListScreen(
    viewModel: WatchlistViewModel,
    openShowDetails: (showId: Int) -> Unit,
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
            .statusBarsPadding(),
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
    onItemClicked: (Int) -> Unit,
) {
    val listState = rememberLazyListState()

    if (viewState.list.isEmpty())
        EmptyWatchlist()
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

@Composable
private fun EmptyWatchlist() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_watchlist_empty),
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface.copy(alpha = 0.8F)),
            modifier = Modifier.size(96.dp),
            contentDescription = null
        )

        ColumnSpacer(value = 12)

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = stringResource(id = R.string.msg_empty_watchlist),
                style = MaterialTheme.typography.body2,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
            )
        }
    }
}