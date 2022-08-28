package com.thomaskioko.showdetails

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ui.Scaffold
import com.thomaskioko.tvmaniac.compose.components.ChoiceChipContent
import com.thomaskioko.tvmaniac.compose.components.CollapsableAppBar
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.components.ErrorView
import com.thomaskioko.tvmaniac.compose.components.ExpandingText
import com.thomaskioko.tvmaniac.compose.components.ExtendedFab
import com.thomaskioko.tvmaniac.compose.components.ExtendedLoadingFab
import com.thomaskioko.tvmaniac.compose.components.KenBurnsViewImage
import com.thomaskioko.tvmaniac.compose.components.LoadingItem
import com.thomaskioko.tvmaniac.compose.components.RowSpacer
import com.thomaskioko.tvmaniac.compose.rememberFlowWithLifecycle
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.theme.backgroundGradient
import com.thomaskioko.tvmaniac.compose.util.copy
import com.thomaskioko.tvmaniac.details.api.interactor.UpdateShowParams
import com.thomaskioko.tvmaniac.details.api.presentation.ShowDetailAction.BookmarkEpisode
import com.thomaskioko.tvmaniac.details.api.presentation.ShowDetailAction.UpdateFollowing
import com.thomaskioko.tvmaniac.details.api.presentation.ShowDetailEffect
import com.thomaskioko.tvmaniac.details.api.presentation.ShowDetailViewState
import com.thomaskioko.tvmaniac.genre.api.GenreUIModel
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.seasons.api.model.SeasonUiModel
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.model.Trailer
import com.thomaskioko.tvmaniac.showcommon.api.model.TvShow

private val HeaderHeight = 550.dp

@Composable
fun ShowDetailScreen(
    viewModel: ShowDetailsViewModel,
    navigateUp: () -> Unit,
    onShowClicked: (Long) -> Unit,
    onSeasonClicked: (Long, String) -> Unit = { _, _ -> },
    onEpisodeClicked: (Long, Long) -> Unit = { _, _ -> },
    onWatchTrailerClicked: (Long, String?) -> Unit = { _, _ -> }
) {

    val viewState by rememberFlowWithLifecycle(viewModel.observeState())
        .collectAsState(initial = ShowDetailViewState.Empty)

    val scaffoldState = rememberScaffoldState()
    val listState = rememberLazyListState()

    LaunchedEffect(viewModel) {
        viewModel.observeSideEffect().collect {
            when (it) {
                is ShowDetailEffect.WatchlistError ->
                    scaffoldState.snackbarHostState
                        .showSnackbar(it.errorMessage)
                is ShowDetailEffect.ShowDetailsError ->
                    scaffoldState.snackbarHostState
                        .showSnackbar(it.errorMessage)
            }
        }
    }


    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            ShowTopBar(
                listState = listState,
                title = viewState.tvShow.title,
                onNavUpClick = navigateUp
            )
        },
        content = { contentPadding ->

            Surface(modifier = Modifier.fillMaxWidth()) {
                if (viewState.errorMessage.isNullOrEmpty()) {
                    TvShowDetailsScrollingContent(
                        detailUiState = viewState,
                        listState = listState,
                        contentPadding = contentPadding,
                        onSeasonClicked = onSeasonClicked,
                        onEpisodeClicked = onEpisodeClicked,
                        onShowClicked = onShowClicked,
                        onUpdateFavoriteClicked = { viewModel.dispatch(UpdateFollowing(it)) },
                        onBookmarkEpClicked = { viewModel.dispatch(BookmarkEpisode(it)) },
                        onWatchTrailerClicked = onWatchTrailerClicked
                    )
                } else {
                    ErrorView()
                }
            }
        }
    )
}

@Composable
private fun ShowTopBar(
    listState: LazyListState,
    title: String,
    onNavUpClick: () -> Unit
) {
    var appBarHeight by remember { mutableStateOf(0) }
    val showAppBarBackground by remember {
        derivedStateOf {
            val visibleItemsInfo = listState.layoutInfo.visibleItemsInfo
            when {
                visibleItemsInfo.isEmpty() -> false
                appBarHeight <= 0 -> false
                else -> {
                    val firstVisibleItem = visibleItemsInfo[0]
                    when {
                        // If the first visible item is > 0, we want to show the app bar background
                        firstVisibleItem.index > 0 -> true
                        // If the first item is visible, only show the app bar background once the only
                        // remaining part of the item is <= the app bar
                        else -> firstVisibleItem.size + firstVisibleItem.offset - 5 <= appBarHeight
                    }
                }
            }
        }
    }

    CollapsableAppBar(
        title = title,
        showAppBarBackground = showAppBarBackground,
        onNavIconPressed = onNavUpClick,
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged { appBarHeight = it.height }
    )
}

@Composable
private fun TvShowDetailsScrollingContent(
    detailUiState: ShowDetailViewState,
    listState: LazyListState,
    contentPadding: PaddingValues,
    onUpdateFavoriteClicked: (UpdateShowParams) -> Unit = {},
    onSeasonClicked: (Long, String) -> Unit = { _, _ -> },
    onEpisodeClicked: (Long, Long) -> Unit = { _, _ -> },
    onBookmarkEpClicked: (Long) -> Unit = { },
    onShowClicked: (Long) -> Unit = {},
    onWatchTrailerClicked: (Long, String?) -> Unit = { _, _ -> },
) {

    LazyColumn(
        state = listState,
        contentPadding = contentPadding.copy(copyTop = false),
    ) {

        item {
            HeaderViewContent(
                detailUiState = detailUiState,
                listState = listState,
                onUpdateFavoriteClicked = onUpdateFavoriteClicked,
                onWatchTrailerClicked = onWatchTrailerClicked
            )
        }

        item {
            BodyContent(
                detailUiState = detailUiState,
                onSeasonClicked = onSeasonClicked,
                onEpisodeClicked = onEpisodeClicked,
                onBookmarkEpClicked = onBookmarkEpClicked,
                onShowClicked = onShowClicked,
                onWatchTrailerClicked = onWatchTrailerClicked
            )
        }

        item {
        }
    }
}

@Composable
private fun HeaderViewContent(
    detailUiState: ShowDetailViewState,
    listState: LazyListState,
    onUpdateFavoriteClicked: (UpdateShowParams) -> Unit,
    onWatchTrailerClicked: (Long, String?) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeaderHeight)
            .clipToBounds()
            .offset {
                IntOffset(
                    x = 0,
                    y = if (listState.firstVisibleItemIndex == 0) {
                        listState.firstVisibleItemScrollOffset / 2
                    } else 0
                )
            }
    ) {
        HeaderImage(
            backdropImageUrl = detailUiState.tvShow.backdropImageUrl
        )

        Body(
            detailUiState = detailUiState,
            onUpdateFavoriteClicked = onUpdateFavoriteClicked,
            onWatchTrailerClicked = onWatchTrailerClicked
        )
    }
}

@Composable
private fun HeaderImage(backdropImageUrl: String) {
    KenBurnsViewImage(
        imageUrl = backdropImageUrl,
        modifier = Modifier
            .fillMaxWidth()
            .height(HeaderHeight)
            .clipToBounds()
    )
}

@Composable
private fun Body(
    detailUiState: ShowDetailViewState,
    onUpdateFavoriteClicked: (UpdateShowParams) -> Unit,
    onWatchTrailerClicked: (Long, String?) -> Unit,
) {
    val surfaceGradient = backgroundGradient().reversed()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeaderHeight)
            .clipToBounds()
            .background(Brush.verticalGradient(surfaceGradient))
            .padding(horizontal = 16.dp)
    ) {
        ColumnSpacer(16)

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = detailUiState.tvShow.title,
                style = MaterialTheme.typography.h4,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            ColumnSpacer(8)

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                ExpandingText(
                    text = detailUiState.tvShow.overview,
                )
            }

            ColumnSpacer(8)

            TvShowMetadata(
                detailUiState = detailUiState,
                onUpdateFavoriteClicked = onUpdateFavoriteClicked,
                onWatchTrailerClicked = onWatchTrailerClicked
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun TvShowMetadata(
    detailUiState: ShowDetailViewState,
    onUpdateFavoriteClicked: (UpdateShowParams) -> Unit,
    onWatchTrailerClicked: (Long, String?) -> Unit,
) {
    val resources = LocalContext.current.resources

    val divider = buildAnnotatedString {
        val tagStyle = MaterialTheme.typography.overline.toSpanStyle().copy(
            color = MaterialTheme.colors.secondary
        )
        withStyle(tagStyle) {
            append("  •  ")
        }
    }
    val text = buildAnnotatedString {
        val tagStyle = MaterialTheme.typography.overline.toSpanStyle().copy(
            color = MaterialTheme.colors.secondary,
            background = MaterialTheme.colors.secondary.copy(alpha = 0.08f)
        )

        AnimatedVisibility(visible = !detailUiState.tvShow.status.isNullOrBlank()) {
            detailUiState.tvShow.status?.let {
                withStyle(tagStyle) {
                    append(" ")
                    append(it)
                    append(" ")
                }
                append(divider)
            }
        }
        append(detailUiState.tvShow.year)

        AnimatedVisibility(visible = detailUiState.tvShow.numberOfSeasons != null) {
            detailUiState.tvShow.numberOfSeasons?.let {
                append(divider)
                append(resources.getQuantityString(R.plurals.season_count, it, it))
            }
        }

        append(divider)
        append(detailUiState.tvShow.language.uppercase())
        append(divider)
        append("${detailUiState.tvShow.averageVotes}")
        append(divider)
    }

    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(
            text = text,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.fillMaxWidth()
        )
    }

    ColumnSpacer(8)

    GenreText(detailUiState.genreUIList)

    ColumnSpacer(8)

    ShowDetailButtons(
        tvShow = detailUiState.tvShow,
        trailerList = detailUiState.trailersList,
        isFollowUpdating = detailUiState.isFollowUpdating,
        isFollowed = detailUiState.isFollowed,
        onUpdateFavoriteClicked = onUpdateFavoriteClicked,
        onWatchTrailerClicked = onWatchTrailerClicked
    )
}

@Composable
private fun GenreText(genreUIList: List<GenreUIModel>) {

    LazyRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(genreUIList) { item ->
            RowSpacer(4)

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                TextButton(
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colors.onBackground,
                        backgroundColor = MaterialTheme.colors.secondary.copy(alpha = 0.08f)
                    ),
                    onClick = {}
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.body2,
                    )
                }
            }
        }
    }
}

@Composable
fun ShowDetailButtons(
    isFollowUpdating: Boolean,
    isFollowed: Boolean,
    tvShow: TvShow,
    trailerList: List<Trailer>,
    onUpdateFavoriteClicked: (UpdateShowParams) -> Unit,
    onWatchTrailerClicked: (Long, String?) -> Unit = { _, _ -> },
) {

    Row(
        horizontalArrangement = Arrangement.Center,
    ) {

        ExtendedFab(
            painter = painterResource(id = R.drawable.ic_trailer_24),
            text = stringResource(id = R.string.btn_trailer),
            onClick = { onWatchTrailerClicked(tvShow.id, trailerList.firstOrNull()?.key) }
        )

        RowSpacer(value = 8)

        val buttonText = if (isFollowed)
            stringResource(id = R.string.unfollow)
        else stringResource(id = R.string.following)

        val imageVector = if (isFollowed)
            painterResource(id = R.drawable.ic_baseline_check_box_24)
        else painterResource(id = R.drawable.ic_baseline_add_box_24)

        ExtendedLoadingFab(
            isLoading = isFollowUpdating,
            painter = imageVector,
            text = buttonText,
            onClick = {
                onUpdateFavoriteClicked(
                    UpdateShowParams(
                        showId = tvShow.id,
                        addToWatchList = isFollowed
                    )
                )
            }
        )
    }
}

@Composable
private fun BodyContent(
    detailUiState: ShowDetailViewState,
    onSeasonClicked: (Long, String) -> Unit,
    onBookmarkEpClicked: (Long) -> Unit,
    onEpisodeClicked: (Long, Long) -> Unit,
    onShowClicked: (Long) -> Unit,
    onWatchTrailerClicked: (Long, String) -> Unit
) {
    LoadingItem(
        isLoading = detailUiState.tvSeasonUiModels.isEmpty()
    ) {
        ColumnSpacer(16)

        SeasonsContent(
            seasonUiModelList = detailUiState.tvSeasonUiModels,
            onSeasonClicked = onSeasonClicked,
            modifier = Modifier.fillMaxWidth()
        )

        EpisodesReleaseContent(
            episodeList = detailUiState.lastAirEpList,
            onEpisodeClicked = onEpisodeClicked,
            onBookmarkEpClicked = onBookmarkEpClicked
        )

        TrailersContent(
            trailersList = detailUiState.trailersList,
            onTrailerClicked = { videoKey ->
                onWatchTrailerClicked(detailUiState.tvShow.id, videoKey)
            }
        )

        SimilarShowsContent(
            similarShows = detailUiState.similarShowList,
            onShowClicked = onShowClicked
        )
    }
}

@Composable
private fun SeasonsContent(
    seasonUiModelList: List<SeasonUiModel>,
    modifier: Modifier,
    onSeasonClicked: (Long, String) -> Unit = { _, _ -> }
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {

        Text(
            text = stringResource(id = R.string.title_seasons),
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .fillMaxWidth()
        )

        val selectedIndex by remember { mutableStateOf(0) }

        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            divider = {}, /* Disable the built-in divider */
            indicator = {},
            edgePadding = 0.dp,
            backgroundColor = Color.Transparent,
            modifier = modifier.fillMaxWidth()
        ) {
            seasonUiModelList.forEach { season ->
                Tab(
                    selected = true,
                    onClick = { onSeasonClicked(season.tvShowId, season.name) }
                ) {
                    ChoiceChipContent(
                        text = season.name,
                        selected = true,
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TvShowDetailsScrollingPreview() {
    TvManiacTheme {
        Surface {
            TvShowDetailsScrollingContent(
                detailUiState = detailUiState,
                listState = LazyListState(),
                contentPadding = PaddingValues(),
                onUpdateFavoriteClicked = {},
                onShowClicked = {},
                onSeasonClicked = { _, _ -> },
            )
        }
    }
}
