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
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.thomaskioko.tvmaniac.R
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.components.KenBurnsViewImage
import com.thomaskioko.tvmaniac.compose.components.LoadingView
import com.thomaskioko.tvmaniac.compose.components.TabItem
import com.thomaskioko.tvmaniac.compose.components.Tabs
import com.thomaskioko.tvmaniac.compose.components.TvManiacScaffold
import com.thomaskioko.tvmaniac.compose.theme.backgroundGradient
import com.thomaskioko.tvmaniac.core.rememberFlowWithLifecycle
import com.thomaskioko.tvmaniac.interactor.EpisodeQuery
import com.thomaskioko.tvmaniac.presentation.model.Season
import com.thomaskioko.tvmaniac.presentation.model.TvShow
import com.thomaskioko.tvmaniac.ui.detail.tabs.EpisodesScreen
import com.thomaskioko.tvmaniac.ui.detail.tabs.SeasonCastScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.*

@Composable
fun ShowDetailScreen(
    viewModel: ShowDetailsViewModel,
    navigateUp: () -> Unit
) {

    val viewState by rememberFlowWithLifecycle(viewModel.uiStateFlow)
        .collectAsState(initial = ShowDetailViewState.Empty)

    TvManiacScaffold(
        content = {
            TvShowDetails(
                viewState,
                onBackPressed = { navigateUp() },
                onSeasonSelected = { viewModel.submitAction(ShowDetailAction.SeasonSelected(it)) }
            )
        }
    )
}

@Composable
fun TvShowDetails(
    detailUiState: ShowDetailViewState,
    onBackPressed: () -> Unit,
    onSeasonSelected: (EpisodeQuery) -> Unit
) {

    val listState = rememberLazyListState()
    var backdropHeight by remember { mutableStateOf(0) }

    TvShowDetailsScrollingContent(
        detailUiState = detailUiState,
        listState = listState,
        onBackdropSizeChanged = { backdropHeight = it.height },
        modifier = Modifier.fillMaxSize(),
        onSeasonSelected = onSeasonSelected,
        onBackPressed = onBackPressed
    )

}

@Composable
private fun TvShowDetailsScrollingContent(
    detailUiState: ShowDetailViewState,
    listState: LazyListState,
    onBackdropSizeChanged: (IntSize) -> Unit,
    modifier: Modifier = Modifier,
    onSeasonSelected: (EpisodeQuery) -> Unit,
    onBackPressed: () -> Unit,
) {

    LazyColumn(
        state = listState,
        modifier = modifier
    ) {

        item { if (detailUiState.isLoading) LoadingView() }

        item {
            TvShowHeaderView(
                detailUiState,
                onBackdropSizeChanged,
                listState = listState,
                onBackPressed = onBackPressed
            )
        }

        item { if(detailUiState.tvSeasons.isNotEmpty()) TvShowSeasons(detailUiState.tvSeasons, onSeasonSelected) }

        item { SeasonEpisodeTabs(detailUiState) }

    }
}

@Composable
fun TvShowHeaderView(
    detailUiState: ShowDetailViewState,
    onBackdropSizeChanged: (IntSize) -> Unit,
    listState: LazyListState,
    onBackPressed: () -> Unit,
) {
    var animateState by remember { mutableStateOf(2) }
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
        LaunchedEffect(Unit) {
            var plus = true
            while (isActive) {
                delay(32)
                animateState += 1 * if (plus) 1 else -1
                plus = !plus
            }
        }
        if (animateState > 0) {
            KenBurnsViewImage(
                imageUrl = detailUiState.tvShow.backdropImageUrl,
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
                    TvShowInfo(detailUiState)
                }
            }
        }
    }
}

@Composable
fun TvShowInfo(detailUiState: ShowDetailViewState) {

    val show = detailUiState.tvShow

    ColumnSpacer(16)
    val padding = Modifier.padding(horizontal = 16.dp)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = show.title,
            style = MaterialTheme.typography.h4,
            modifier = padding,
            maxLines = 1
        )

        ColumnSpacer(8)

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = show.overview,
                style = MaterialTheme.typography.body2,
                maxLines = 4,
                modifier = padding
            )
        }

        ColumnSpacer(8)

    }

    TvShowMetadata(
        show = show,
        seasons = detailUiState.tvSeasons,
        modifier = Modifier.padding(horizontal = 16.dp)
    )

    Spacer(Modifier.height(16.dp))
}

@Composable
fun TvShowMetadata(show: TvShow, modifier: Modifier, seasons: List<Season>) {
    val resources = LocalContext.current.resources
    val divider = "  •  "
    val text = buildAnnotatedString {
        val tagStyle = MaterialTheme.typography.overline.toSpanStyle().copy(
            background = MaterialTheme.colors.primary.copy(alpha = 0.8f)
        )
        withStyle(tagStyle) {
            append("  Tv Show  ")
        }
        append(divider)
        append(show.year)
        append(divider)
        append(resources.getQuantityString(R.plurals.season_count, seasons.size, seasons.size))
        append(divider)
        append(show.language.toUpperCase(Locale.ENGLISH))
        append(divider)
        append("${show.averageVotes}")
        append(divider)
    }
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(
            text = text,
            style = MaterialTheme.typography.body2,
            modifier = modifier
        )
    }
}

@Composable
fun TvShowSeasons(
    tvSeasons: List<Season>,
    onSeasonSelected: (EpisodeQuery) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }
    var selectedPosition by remember { mutableStateOf(0) }

    if (tvSeasons.isNotEmpty() && selectedPosition == 0) {
        onSeasonSelected(
            EpisodeQuery(
                tvShowId = tvSeasons.first().tvShowId,
                seasonId = tvSeasons.first().seasonId,
                seasonNumber = tvSeasons.first().seasonNumber
            )
        )
    }

    var selectedSeasonText by remember { mutableStateOf(tvSeasons.first().name) }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        TextButton(
            onClick = { expanded = !expanded },
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colors.onBackground,
                backgroundColor = Color.Transparent
            )
        ) {
            Text(
                text = selectedSeasonText,
                style = MaterialTheme.typography.body1
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            tvSeasons.forEachIndexed { index, season ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        selectedPosition = index
                        selectedSeasonText = season.name

                        onSeasonSelected(
                            EpisodeQuery(
                                tvShowId = season.tvShowId,
                                seasonId = season.seasonId,
                                seasonNumber = season.seasonNumber
                            )
                        )
                    }) {

                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(
                            text = season.name,
                            style = MaterialTheme.typography.body2,
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun SeasonEpisodeTabs(viewState: ShowDetailViewState) {

    Column {
        val tabs = listOf(
            TabItem.Episodes,
            TabItem.Casts,
        )

        val pagerState = rememberPagerState(pageCount = tabs.size)

        Tabs(tabs = tabs, pagerState = pagerState)

        HorizontalPager(state = pagerState) { page ->
            when (tabs[page]) {
                TabItem.Casts -> SeasonCastScreen()
                TabItem.Episodes -> EpisodesScreen(viewState.episodesViewState)
            }
        }
    }

}