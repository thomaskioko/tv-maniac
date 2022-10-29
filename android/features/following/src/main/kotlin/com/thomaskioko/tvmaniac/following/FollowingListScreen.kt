package com.thomaskioko.tvmaniac.following

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.EmptyContentView
import com.thomaskioko.tvmaniac.compose.components.SwipeDismissSnackbar
import com.thomaskioko.tvmaniac.resources.R

@Composable
fun FollowingContent(
    viewModel: FollowingViewModel,
    openShowDetails: (showId: Int) -> Unit,
) {

    val followedViewState by viewModel.observeState().collectAsStateWithLifecycle()

    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(Unit) {
        viewModel.observeSideEffect().collect {
            when (it) {
                is FollowingEffect.Error -> scaffoldState.snackbarHostState.showSnackbar(it.message)
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
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
                    .fillMaxWidth()
            )
        },
        content = { contentPadding ->
            FollowingGridContent(
                viewState = followedViewState,
                paddingValues = contentPadding,
                onItemClicked = openShowDetails
            )
        }
    )
}

@Composable
private fun FollowingGridContent(
    viewState: FollowingState,
    paddingValues: PaddingValues,
    onItemClicked: (Int) -> Unit,
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
            items = viewState.list,
            paddingValues = paddingValues,
        ) { show ->
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
                            .weight(1f)
                            .aspectRatio(2 / 3f)
                    )
                }
            }
        }
}
