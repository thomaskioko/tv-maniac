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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.insets.ui.Scaffold
import com.thomaskioko.tvmaniac.compose.components.ChoiceChipContent
import com.thomaskioko.tvmaniac.compose.components.CollapsableAppBar
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.components.ExpandingText
import com.thomaskioko.tvmaniac.compose.components.ExtendedFab
import com.thomaskioko.tvmaniac.compose.components.ExtendedLoadingFab
import com.thomaskioko.tvmaniac.compose.components.FullScreenLoading
import com.thomaskioko.tvmaniac.compose.components.KenBurnsViewImage
import com.thomaskioko.tvmaniac.compose.components.LoadingRowContent
import com.thomaskioko.tvmaniac.compose.components.RowSpacer
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.theme.backgroundGradient
import com.thomaskioko.tvmaniac.compose.util.copy
import com.thomaskioko.tvmaniac.details.api.FollowShow
import com.thomaskioko.tvmaniac.details.api.SeasonState
import com.thomaskioko.tvmaniac.details.api.ShowDetailsAction
import com.thomaskioko.tvmaniac.details.api.ShowDetailsState
import com.thomaskioko.tvmaniac.details.api.SimilarShowsState
import com.thomaskioko.tvmaniac.details.api.TrailersState
import com.thomaskioko.tvmaniac.details.api.model.Season
import com.thomaskioko.tvmaniac.details.api.model.Show
import com.thomaskioko.tvmaniac.details.api.model.Trailer
import com.thomaskioko.tvmaniac.resources.R

private val HeaderHeight = 550.dp

@Composable
fun ShowDetailScreen(
    viewModel: ShowDetailsViewModel,
    navigateUp: () -> Unit,
    onShowClicked: (Int) -> Unit,
    onSeasonClicked: (Int, String) -> Unit,
    onWatchTrailerClicked: (Int, String?) -> Unit = { _, _ -> }
) {

    val viewState by viewModel.state.collectAsStateWithLifecycle()

    val scaffoldState = rememberScaffoldState()
    val listState = rememberLazyListState()

    val title = (viewState as? ShowDetailsState.ShowDetailsLoaded)?.show?.title ?: ""

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            ShowTopBar(
                listState = listState,
                title = title,
                onNavUpClick = navigateUp
            )
        },
        content = { contentPadding ->

            ShowDetailContent(
                viewState,
                listState,
                contentPadding,
                onSeasonClicked,
                onShowClicked,
                onWatchTrailerClicked,
                onUpdateFavoriteClicked = { viewModel.dispatch(it) }
            )
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
private fun ShowDetailContent(
    viewState: ShowDetailsState,
    listState: LazyListState,
    contentPadding: PaddingValues,
    onSeasonClicked: (Int, String) -> Unit,
    onShowClicked: (Int) -> Unit,
    onWatchTrailerClicked: (Int, String?) -> Unit,
    onUpdateFavoriteClicked: (ShowDetailsAction) -> Unit,
) {
    LazyColumn(
        state = listState,
        contentPadding = contentPadding.copy(copyTop = false),
    ) {
        when (viewState) {
            ShowDetailsState.Loading -> item { FullScreenLoading() }
            is ShowDetailsState.ShowDetailsLoaded -> {
                item {
                    HeaderViewContent(
                        listState = listState,
                        show = viewState.show,
                        trailersList = (detailUiState.trailerState as? TrailersState.TrailersLoaded)?.trailersList,
                        onUpdateFavoriteClicked = onUpdateFavoriteClicked,
                        onWatchTrailerClicked = onWatchTrailerClicked
                    )
                }

                when (viewState.seasonState) {
                    is SeasonState.SeasonsLoaded -> item {
                        val state = (viewState.seasonState as SeasonState.SeasonsLoaded)
                        SeasonsUi(
                            isLoading = state.isLoading,
                            seasonsList = state.seasonsList,
                            onSeasonClicked = onSeasonClicked
                        )
                    }
                    is SeasonState.SeasonsError -> {
                        //TODO:: Show Error view with retry
                    }
                }

                when (viewState.trailerState) {
                    is TrailersState.TrailersError -> {
                        //TODO:: Show Error view with retry
                    }
                    is TrailersState.TrailersLoaded -> item {
                        val state = (viewState.trailerState as TrailersState.TrailersLoaded)
                        TrailersContent(
                            isLoading = state.isLoading,
                            trailersList = state.trailersList,
                            onTrailerClicked = { videoKey ->
                                onWatchTrailerClicked(detailUiState.show.traktId, videoKey)
                            }
                        )
                    }
                }

                when (viewState.similarShowsState) {
                    is SimilarShowsState.SimilarShowsError -> {
                        //TODO:: Show Error view with retry
                    }
                    is SimilarShowsState.SimilarShowsLoaded -> item {
                        val state =
                            viewState.similarShowsState as SimilarShowsState.SimilarShowsLoaded
                        SimilarShowsUi(
                            isLoading = state.isLoading,
                            similarShows = state.similarShows,
                            onShowClicked = onShowClicked
                        )
                    }
                }
            }

            is ShowDetailsState.ShowDetailsError -> item { ErrorUi(onRetry = {}) }

        }
    }
}

@Composable
private fun SeasonsUi(
    isLoading: Boolean,
    seasonsList: List<Season>,
    onSeasonClicked: (Int, String) -> Unit
) {
    LoadingRowContent(
        isLoading = isLoading,
        text = stringResource(id = R.string.title_seasons)
    ) {
        val selectedIndex by remember { mutableStateOf(0) }

        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            divider = {}, /* Disable the built-in divider */
            indicator = {},
            edgePadding = 0.dp,
            backgroundColor = Color.Transparent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
        ) {
            seasonsList
                .forEach { season ->
                    Tab(
                        selected = true,
                        onClick = {
                            onSeasonClicked(
                                season.tvShowId,
                                season.name
                            )
                        }
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

@Composable
private fun HeaderViewContent(
    show: Show,
    trailersList: List<Trailer>?,
    listState: LazyListState,
    onUpdateFavoriteClicked: (ShowDetailsAction) -> Unit,
    onWatchTrailerClicked: (Int, String?) -> Unit,
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
            backdropImageUrl = show.backdropImageUrl
        )

        Body(
            show = show,
            trailersList = trailersList,
            onUpdateFavoriteClicked = onUpdateFavoriteClicked,
            onWatchTrailerClicked = onWatchTrailerClicked
        )
    }
}

@Composable
private fun HeaderImage(backdropImageUrl: String?) {
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
    show: Show,
    trailersList: List<Trailer>?,
    onUpdateFavoriteClicked: (ShowDetailsAction) -> Unit,
    onWatchTrailerClicked: (Int, String?) -> Unit,
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
                text = show.title,
                style = MaterialTheme.typography.h4,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            ColumnSpacer(8)

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                ExpandingText(text = show.overview)
            }

            ColumnSpacer(8)

            TvShowMetadata(
                show = show,
                trailersList = trailersList,
                onUpdateFavoriteClicked = onUpdateFavoriteClicked,
                onWatchTrailerClicked = onWatchTrailerClicked
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun TvShowMetadata(
    show: Show,
    trailersList: List<Trailer>?,
    onUpdateFavoriteClicked: (ShowDetailsAction) -> Unit,
    onWatchTrailerClicked: (Int, String?) -> Unit,
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

        AnimatedVisibility(visible = !show.status.isNullOrBlank()) {
            show.status?.let {
                withStyle(tagStyle) {
                    append(" ")
                    append(it)
                    append(" ")
                }
                append(divider)
            }
        }
        append(show.year)

        AnimatedVisibility(visible = show.numberOfSeasons != null) {
            show.numberOfSeasons?.let {
                append(divider)
                append(resources.getQuantityString(R.plurals.season_count, it, it))
            }
        }

        append(divider)
        show.language?.let { language ->
            append(language)
            append(divider)
        }
        append("${show.rating}")
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

    GenreText(show.genres)

    ColumnSpacer(8)

    ShowDetailButtons(
        traktId = show.traktId,
        trailersList = trailersList,
        isFollowed = show.isFollowed,
        onUpdateFavoriteClicked = onUpdateFavoriteClicked,
        onWatchTrailerClicked = onWatchTrailerClicked
    )

    ColumnSpacer(8)
}

@Composable
private fun GenreText(genreList: List<String>) {

    LazyRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(genreList) { genre ->
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
                        text = genre,
                        style = MaterialTheme.typography.body2,
                    )
                }
            }
        }
    }
}

@Composable
fun ShowDetailButtons(
    isFollowed: Boolean,
    traktId: Int,
    trailersList: List<Trailer>?,
    onUpdateFavoriteClicked: (ShowDetailsAction) -> Unit,
    onWatchTrailerClicked: (Int, String?) -> Unit = { _, _ -> },
) {

    val trailerKey = trailersList?.firstOrNull()?.key

    Row(
        horizontalArrangement = Arrangement.Center,
    ) {

        ExtendedFab(
            painter = painterResource(id = R.drawable.ic_trailer_24),
            text = stringResource(id = R.string.btn_trailer),
            onClick = { onWatchTrailerClicked(traktId, trailerKey) }
        )

        RowSpacer(value = 8)

        val buttonText = if (isFollowed)
            stringResource(id = R.string.unfollow)
        else stringResource(id = R.string.following)

        val imageVector = if (isFollowed)
            painterResource(id = R.drawable.ic_baseline_check_box_24)
        else painterResource(id = R.drawable.ic_baseline_add_box_24)

        ExtendedLoadingFab(
            painter = imageVector,
            text = buttonText,
            onClick = {
                onUpdateFavoriteClicked(
                    FollowShow(
                        traktId = traktId,
                        addToWatchList = isFollowed,
                    )
                )
            }
        )
    }
}


@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TvShowDetailsScrollingPreview() {
    TvManiacTheme {
        Surface {
            ShowDetailContent(
                viewState = detailUiState,
                listState = LazyListState(),
                contentPadding = PaddingValues(),
                onShowClicked = {},
                onSeasonClicked = { _, _ -> },
                onWatchTrailerClicked = { _, _ -> },
                onUpdateFavoriteClicked = {}
            )
        }
    }
}
