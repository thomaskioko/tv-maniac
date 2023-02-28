package com.thomaskioko.tvmaniac.following

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.EmptyContentView
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.components.FullScreenLoading
import com.thomaskioko.tvmaniac.domain.following.ErrorLoadingShows
import com.thomaskioko.tvmaniac.domain.following.FollowedShow
import com.thomaskioko.tvmaniac.domain.following.FollowingContent
import com.thomaskioko.tvmaniac.domain.following.LoadingShows
import com.thomaskioko.tvmaniac.domain.following.ReloadFollowedShows
import com.thomaskioko.tvmaniac.resources.R

@Composable
fun FollowingScreen(
    viewModel: FollowingViewModel,
    openShowDetails: (showId: Long) -> Unit,
) {

    val followedState by viewModel.state.collectAsStateWithLifecycle()
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        content = { contentPadding ->

            when (followedState) {
                is LoadingShows -> FullScreenLoading()
                is ErrorLoadingShows -> ErrorUi(
                    errorMessage = (followedState as ErrorLoadingShows).message,
                    onRetry = { viewModel.dispatch(ReloadFollowedShows) })

                is FollowingContent -> {
                    val state = (followedState as FollowingContent)
                    when {
                        state.list.isEmpty() -> EmptyContentView(
                            painter = painterResource(id = R.drawable.ic_watchlist_empty),
                            message = stringResource(id = R.string.msg_empty_favorites)
                        )

                        else -> FollowingGridContent(
                            list = state.list,
                            paddingValues = contentPadding,
                            onItemClicked = openShowDetails
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun FollowingGridContent(
    list: List<FollowedShow>,
    paddingValues: PaddingValues,
    onItemClicked: (Long) -> Unit,
) {
    val listState = rememberLazyListState()

    LazyGridItems(
        listState = listState,
        items = list,
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
