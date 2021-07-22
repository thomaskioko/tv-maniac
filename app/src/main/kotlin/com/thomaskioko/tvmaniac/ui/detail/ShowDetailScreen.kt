package com.thomaskioko.tvmaniac.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.components.ErrorView
import com.thomaskioko.tvmaniac.compose.components.KenBurnsViewImage
import com.thomaskioko.tvmaniac.compose.components.LoadingView
import com.thomaskioko.tvmaniac.compose.components.TvManiacScaffold
import com.thomaskioko.tvmaniac.compose.theme.backgroundGradient
import com.thomaskioko.tvmaniac.datasource.cache.model.TvShow
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.*

@Composable
fun ShowDetailScreen(
    viewModel: ShowDetailsViewModel,
    navController: NavHostController,
) {

    val repoId = navController.currentBackStackEntry?.arguments?.getString("tvShowId")
    if (repoId != null) {
        viewModel.dispatchAction(ShowDetailsAction.LoadShowDetails(repoId.toInt()))
    } else {
        ErrorView()
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val actionState = viewModel.stateFlow

    val actionStateLifeCycleAware = remember(actionState, lifecycleOwner) {
        actionState.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
    }

    val detailViewState by actionStateLifeCycleAware
        .collectAsState(initial = ShowDetailsViewState.Loading)

    TvManiacScaffold(
        content = {
            DetailPageContent(viewState = detailViewState)
        }
    )
}

@Composable
fun DetailPageContent(
    viewState: ShowDetailsViewState
) {

    when (viewState) {
        is ShowDetailsViewState.Error -> ErrorView(viewState.message)
        ShowDetailsViewState.Loading -> LoadingView()
        is ShowDetailsViewState.Success -> TvShowDetails(viewState.data)
    }
}

@Composable
fun TvShowDetails(entity: TvShow) {

    var animateState by remember { mutableStateOf(2) }
    val listState = rememberLazyListState()
    var backdropHeight by remember { mutableStateOf(0) }


    LaunchedEffect(Unit) {
        var plus = true
        while (isActive) {
            delay(32)
            animateState += 1 * if (plus) 1 else -1
            plus = !plus
        }
    }

    Surface(Modifier.fillMaxSize()) {
        TvShowDetailsScrollingContent(
            animateState = animateState,
            tvShow = entity,
            listState = listState,
            onBackdropSizeChanged = { backdropHeight = it.height },
            modifier = Modifier.fillMaxSize()
        )
    }

}

@Composable
private fun TvShowDetailsScrollingContent(
    animateState: Int,
    tvShow: TvShow,
    listState: LazyListState,
    onBackdropSizeChanged: (IntSize) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = listState,
        modifier = modifier
    ) {

        item {
            TvShowHeaderView(
                tvShow,
                animateState,
                onBackdropSizeChanged,
                listState = listState
            )
        }
    }
}

@Composable
fun TvShowHeaderView(
    show: TvShow,
    animateState: Int,
    onBackdropSizeChanged: (IntSize) -> Unit,
    listState: LazyListState,
) {

    val surfaceGradient = backgroundGradient().reversed()

    val headerHeight by remember { mutableStateOf(450) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
            .background(
                Brush.verticalGradient(
                    surfaceGradient,
                    0F,
                    headerHeight.toFloat(),
                    TileMode.Clamp
                )
            )
    ) {
        if (animateState > 0) {
            KenBurnsViewImage(
                imageUrl = show.posterImageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged(onBackdropSizeChanged)
                    .clipToBounds()
                    .height(headerHeight.dp)
                    .offset {
                        IntOffset(
                            x = 0,
                            y = if (listState.firstVisibleItemIndex == 0) {
                                listState.firstVisibleItemScrollOffset / 2
                            } else 0
                        )
                    }
            )
        }

        BoxWithConstraints {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight.dp)
                    .background(
                        Brush.verticalGradient(
                            surfaceGradient,
                            0F,
                            constraints.maxHeight.toFloat(),
                            TileMode.Clamp
                        )
                    )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    TvShowInfo(show)
                }
            }
        }
    }
}

@Composable
fun TvShowInfo(show: TvShow) {

    ColumnSpacer(16)
    val padding = Modifier.padding(horizontal = 16.dp)

    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {

        item {
            Text(
                text = show.title,
                style = MaterialTheme.typography.h4,
                modifier = padding,
                maxLines = 1
            )
        }

        item { ColumnSpacer(8) }

        item {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = show.overview,
                    style = MaterialTheme.typography.body2,
                    maxLines = 4,
                    modifier = padding
                )
            }
        }

        item { ColumnSpacer(8) }

    }

    TvShowMetadata(show = show, modifier = Modifier.padding(horizontal = 16.dp))
    Spacer(Modifier.height(16.dp))
}

@Composable
fun TvShowMetadata(show: TvShow, modifier: Modifier) {
    val divider = "  •  "
    val text = buildAnnotatedString {
        val tagStyle = MaterialTheme.typography.overline.toSpanStyle().copy(
            background = MaterialTheme.colors.primary.copy(alpha = 0.8f)
        )
        withStyle(tagStyle) {
            append("  Tv Show  ")
        }
        append(divider)
        append(show.language.toUpperCase(Locale.ENGLISH))
        append(divider)
        append("${show.averageVotes}")
    }
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(
            text = text,
            style = MaterialTheme.typography.body2,
            modifier = modifier
        )
    }
}
