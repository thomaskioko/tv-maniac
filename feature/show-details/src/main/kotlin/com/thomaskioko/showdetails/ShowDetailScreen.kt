package com.thomaskioko.showdetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.thomaskioko.showdetails.DetailConstants.HEADER_HEIGHT
import com.thomaskioko.tvmaniac.common.navigation.TvManiacScreens
import com.thomaskioko.tvmaniac.common.navigation.TvManiacScreens.ShowDetailsScreen
import com.thomaskioko.tvmaniac.common.voyagerutil.viewModel
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.CollapsableAppBar
import com.thomaskioko.tvmaniac.compose.components.ExpandingText
import com.thomaskioko.tvmaniac.compose.components.KenBurnsViewImage
import com.thomaskioko.tvmaniac.compose.components.SnackBarErrorRetry
import com.thomaskioko.tvmaniac.compose.components.TextLoadingItem
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacChip
import com.thomaskioko.tvmaniac.compose.components.TvManiacOutlinedButton
import com.thomaskioko.tvmaniac.compose.components.TvManiacTextButton
import com.thomaskioko.tvmaniac.compose.components.TvPosterCard
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.theme.backgroundGradient
import com.thomaskioko.tvmaniac.presentation.showdetails.DismissWebViewError
import com.thomaskioko.tvmaniac.presentation.showdetails.FollowShowClicked
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsAction
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsState
import com.thomaskioko.tvmaniac.presentation.showdetails.WebViewError
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Season
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Show
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Trailer
import com.thomaskioko.tvmaniac.resources.R
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.collections.immutable.ImmutableList

data class ShowDetailsScreen(val id: Long) : Screen {
    override val key: ScreenKey = "_show_details_$id"

    @Composable
    override fun Content() {
        val screenModel = viewModel { showDetailsScreenModel(id) }
        val state by screenModel.state.collectAsStateWithLifecycle()

        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }
        val listState = rememberLazyListState()

        ShowDetailsUi(
            state = state,
            title = state.show.title,
            onBackClicked = { navigator.pop() },
            onSeasonClicked = { id, season -> },
            onShowClicked = { navigator.push(ScreenRegistry.get(ShowDetailsScreen(it))) },
            onWatchTrailerClicked = { id ->
                navigator.push(ScreenRegistry.get(TvManiacScreens.TrailersScreen(id)))
            },
            snackbarHostState = snackbarHostState,
            listState = listState,
            onAction = screenModel::dispatch,
        )
    }
}

@Composable
internal fun ShowDetailsUi(
    state: ShowDetailsState,
    title: String,
    onBackClicked: () -> Unit,
    onSeasonClicked: (Long, String) -> Unit,
    onShowClicked: (Long) -> Unit,
    onWatchTrailerClicked: (Long) -> Unit,
    snackbarHostState: SnackbarHostState,
    listState: LazyListState,
    onAction: (ShowDetailsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            ShowTopBar(
                listState = listState,
                title = title,
                onNavUpClick = onBackClicked,
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        content = { contentPadding ->

            ShowDetailsUi(
                show = state.show,
                trailerContent = state.trailersContent,
                seasonsContent = state.seasonsContent,
                similarShowsContent = state.similarShowsContent,
                contentPadding = contentPadding,
                snackBarHostState = snackbarHostState,
                listState = listState,
                modifier = modifier,
                onSeasonClicked = onSeasonClicked,
                onShowClicked = onShowClicked,
                onWatchTrailerClicked = onWatchTrailerClicked,
                onAction = onAction,
            )
        },
    )
}

@Composable
private fun ShowTopBar(
    listState: LazyListState,
    title: String,
    onNavUpClick: () -> Unit,
) {
    var appBarHeight by remember { mutableIntStateOf(0) }
    val showAppBarBackground by remember {
        derivedStateOf {
            val visibleItemsInfo = listState.layoutInfo.visibleItemsInfo
            when {
                visibleItemsInfo.isEmpty() -> false
                appBarHeight <= 0 -> false
                else -> {
                    val firstVisibleItem = visibleItemsInfo[0]
                    when {
                        firstVisibleItem.index > 0 -> true
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
            .onSizeChanged { appBarHeight = it.height },
    )
}

@Composable
private fun ShowDetailsUi(
    show: Show,
    trailerContent: ShowDetailsState.TrailersContent,
    seasonsContent: ShowDetailsState.SeasonsContent,
    similarShowsContent: ShowDetailsState.SimilarShowsContent,
    listState: LazyListState,
    snackBarHostState: SnackbarHostState,
    contentPadding: PaddingValues,
    onSeasonClicked: (Long, String) -> Unit,
    onShowClicked: (Long) -> Unit,
    onWatchTrailerClicked: (Long) -> Unit,
    onAction: (ShowDetailsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding.copy(copyTop = false),
    ) {
        item {
            HeaderContent(
                listState = listState,
                show = show,
                onUpdateFavoriteClicked = { onAction(FollowShowClicked(it)) },
                onWatchTrailerClicked = onWatchTrailerClicked,
            )
        }

        item {
            SeasonsContent(
                isLoading = seasonsContent.isLoading,
                seasonsList = seasonsContent.seasonsList,
                onSeasonClicked = onSeasonClicked,
            )
        }

        item {
            TrailersContent(
                trailersState = trailerContent,
                snackBarHostState = snackBarHostState,
                onDismissTrailerErrorClicked = { onAction(DismissWebViewError) },
                onWatchTrailerClicked = onWatchTrailerClicked,
                onAction = onAction,
            )
        }

        item {
            SimilarShowsContent(
                isLoading = similarShowsContent.isLoading,
                similarShows = similarShowsContent.similarShows,
                onShowClicked = onShowClicked,
            )
        }
    }
}

@Composable
private fun HeaderContent(
    show: Show?,
    listState: LazyListState,
    onUpdateFavoriteClicked: (Boolean) -> Unit,
    onWatchTrailerClicked: (Long) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HEADER_HEIGHT)
            .clipToBounds()
            .offset {
                IntOffset(
                    x = 0,
                    y = if (listState.firstVisibleItemIndex == 0) {
                        listState.firstVisibleItemScrollOffset / 2
                    } else {
                        0
                    },
                )
            },
    ) {
        KenBurnsViewImage(
            imageUrl = show?.backdropImageUrl,
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
        )

        if (show != null) {
            Body(
                show = show,
                onUpdateFavoriteClicked = onUpdateFavoriteClicked,
                onWatchTrailerClicked = onWatchTrailerClicked,
            )
        }
    }
}

@Composable
private fun Body(
    show: Show,
    onUpdateFavoriteClicked: (Boolean) -> Unit,
    onWatchTrailerClicked: (Long) -> Unit,
) {
    val surfaceGradient = backgroundGradient().reversed()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .background(Brush.verticalGradient(surfaceGradient))
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = show.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )

            Spacer(modifier = Modifier.height(8.dp))

            ExpandingText(
                text = show.overview,
                textStyle = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
            )

            Spacer(modifier = Modifier.height(8.dp))

            ShowMetadata(
                releaseYear = show.year,
                status = show.status,
                seasonNumber = show.numberOfSeasons,
                language = show.language,
                rating = show.rating,
            )

            GenreText(show.genres)

            Spacer(modifier = Modifier.height(8.dp))

            ShowDetailButtons(
                traktId = show.traktId,
                isFollowed = show.isFollowed,
                onUpdateFavoriteClicked = onUpdateFavoriteClicked,
                onWatchTrailerClicked = onWatchTrailerClicked,
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun ShowMetadata(
    releaseYear: String,
    status: String?,
    seasonNumber: Int?,
    language: String?,
    rating: Double,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        val resources = LocalContext.current.resources

        val divider = buildAnnotatedString {
            val tagStyle = MaterialTheme.typography.labelMedium.toSpanStyle().copy(
                color = MaterialTheme.colorScheme.secondary,
            )
            withStyle(tagStyle) {
                append("  •  ")
            }
        }
        val text = buildAnnotatedString {
            val statusStyle = MaterialTheme.typography.labelMedium.toSpanStyle().copy(
                color = MaterialTheme.colorScheme.secondary,
                background = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
            )

            val tagStyle = MaterialTheme.typography.labelMedium.toSpanStyle().copy(
                color = MaterialTheme.colorScheme.onSurface,
            )

            AnimatedVisibility(visible = !status.isNullOrBlank()) {
                status?.let {
                    withStyle(statusStyle) {
                        append(" ")
                        append(it)
                        append(" ")
                    }
                    append(divider)
                }
            }

            withStyle(tagStyle) {
                append(releaseYear)
            }

            AnimatedVisibility(visible = seasonNumber != null) {
                seasonNumber?.let {
                    append(divider)
                    withStyle(tagStyle) {
                        append(resources.getQuantityString(R.plurals.season_count, it, it))
                    }
                }
            }

            append(divider)
            language?.let { language ->
                withStyle(tagStyle) {
                    append(language)
                }
                append(divider)
            }
            withStyle(tagStyle) {
                append("$rating")
            }
            append(divider)
        }

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun GenreText(
    genreList: ImmutableList<String>,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(genreList) { genre ->

            Spacer(modifier = Modifier.width(4.dp))

            TvManiacTextButton(
                onClick = {},
                shape = RoundedCornerShape(4.dp),
                buttonColors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
                ),
                content = {
                    Text(
                        text = genre,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                },
            )
        }
    }
}

@Composable
fun ShowDetailButtons(
    isFollowed: Boolean,
    traktId: Long,
    onUpdateFavoriteClicked: (Boolean) -> Unit,
    onWatchTrailerClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
    ) {
        TvManiacOutlinedButton(
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_trailer_24),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(
                        MaterialTheme.colorScheme.secondary.copy(
                            alpha = 0.8F,
                        ),
                    ),
                )
            },
            text = stringResource(id = R.string.btn_trailer),
            textPadding = 8.dp,
            borderColor = MaterialTheme.colorScheme.secondary,
            onClick = { onWatchTrailerClicked(traktId) },
        )

        Spacer(modifier = Modifier.width(8.dp))

        TvManiacOutlinedButton(
            leadingIcon = {
                Image(
                    painter = if (isFollowed) {
                        painterResource(id = R.drawable.ic_baseline_check_box_24)
                    } else {
                        painterResource(id = R.drawable.ic_baseline_add_box_24)
                    },
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(
                        MaterialTheme.colorScheme.secondary.copy(
                            alpha = 0.8F,
                        ),
                    ),
                )
            },
            text = if (isFollowed) {
                stringResource(id = R.string.unfollow)
            } else {
                stringResource(id = R.string.following)
            },
            textPadding = 8.dp,
            onClick = { onUpdateFavoriteClicked(isFollowed) },
            borderColor = MaterialTheme.colorScheme.secondary,
        )
    }
}

@Composable
private fun SeasonsContent(
    isLoading: Boolean,
    seasonsList: ImmutableList<Season>,
    onSeasonClicked: (Long, String) -> Unit,
) {
    if (seasonsList.isNotEmpty()) {
        TextLoadingItem(
            isLoading = isLoading,
            text = stringResource(id = R.string.title_seasons),
        )
        val selectedIndex by remember { mutableIntStateOf(0) }

        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            divider = {}, /* Disable the built-in divider */
            indicator = {},
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
        ) {
            seasonsList.forEach { season ->
                Tab(
                    modifier = Modifier
                        .padding(end = 4.dp),
                    selected = true,
                    onClick = {
                        onSeasonClicked(
                            season.tvShowId,
                            season.name,
                        )
                    },
                ) {
                    TvManiacChip(
                        text = season.name,
                        onClick = {
                            onSeasonClicked(
                                season.tvShowId,
                                season.name,
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun TrailersContent(
    trailersState: ShowDetailsState.TrailersContent,
    snackBarHostState: SnackbarHostState,
    onDismissTrailerErrorClicked: () -> Unit,
    onAction: (ShowDetailsAction) -> Unit,
    onWatchTrailerClicked: (Long) -> Unit,
) {
    SnackBarErrorRetry(
        snackBarHostState = snackBarHostState,
        errorMessage = trailersState.playerErrorMessage,
        onErrorAction = onDismissTrailerErrorClicked,
        actionLabel = "Dismiss",
    )

    if (trailersState.trailersList.isNotEmpty()) {
        TrailersRowContent(
            isLoading = trailersState.isLoading,
            trailersList = trailersState.trailersList,
            onTrailerClicked = { showId ->
                if (trailersState.hasWebViewInstalled) {
                    onWatchTrailerClicked(showId)
                } else {
                    onAction(WebViewError)
                }
            },
        )
    }
}

@OptIn(ExperimentalSnapperApi::class, ExperimentalFoundationApi::class)
@Composable
fun SimilarShowsContent(
    isLoading: Boolean,
    similarShows: ImmutableList<Show>,
    modifier: Modifier = Modifier,
    onShowClicked: (Long) -> Unit = {},
) {
    val lazyListState = rememberLazyListState()

    if (similarShows.isNotEmpty()) {
        TextLoadingItem(
            isLoading = isLoading,
            text = stringResource(id = R.string.title_similar),
        )

        LazyRow(
            modifier = modifier,
            state = lazyListState,
            flingBehavior = rememberSnapperFlingBehavior(lazyListState),
        ) {
            itemsIndexed(similarShows) { index, tvShow ->
                val value = if (index == 0) 16 else 4

                Spacer(modifier = Modifier.width(value.dp))

                TvPosterCard(
                    modifier = Modifier
                        .animateItemPlacement(),
                    posterImageUrl = tvShow.posterImageUrl,
                    title = tvShow.title,
                    onClick = { onShowClicked(tvShow.traktId) },
                    imageWidth = 84.dp,
                )
            }
        }
    }
}

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun TrailersRowContent(
    isLoading: Boolean,
    trailersList: ImmutableList<Trailer>,
    modifier: Modifier = Modifier,
    onTrailerClicked: (Long) -> Unit,
) {
    TextLoadingItem(
        isLoading = isLoading,
        text = stringResource(id = R.string.title_trailer),
    )

    val lazyListState = rememberLazyListState()

    LazyRow(
        modifier = modifier,
        state = lazyListState,
        flingBehavior = rememberSnapperFlingBehavior(lazyListState),
    ) {
        itemsIndexed(trailersList) { index, trailer ->

            val value = if (index == 0) 16 else 8
            Spacer(modifier = Modifier.width(value.dp))

            Card(
                modifier = Modifier
                    .clickable { onTrailerClicked(trailer.showId) },
                shape = RoundedCornerShape(4.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp,
                ),
            ) {
                Box {
                    AsyncImageComposable(
                        model = trailer.youtubeThumbnailUrl,
                        contentDescription = trailer.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(140.dp)
                            .aspectRatio(3 / 1.5f)
                            .drawWithCache {
                                val gradient = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black),
                                    startY = size.height / 3,
                                    endY = size.height,
                                )
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(gradient, blendMode = BlendMode.Multiply)
                                }
                            },
                    )

                    Icon(
                        imageVector = Icons.Filled.PlayCircle,
                        contentDescription = trailer.name,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(48.dp),
                    )
                }
            }
        }
    }
}

private object DetailConstants {
    val HEADER_HEIGHT = 550.dp
}

@ThemePreviews
@Composable
private fun ShowDetailScreenPreview(
    @PreviewParameter(DetailPreviewParameterProvider::class)
    state: ShowDetailsState,
) {
    TvManiacTheme {
        Surface {
            ShowDetailsUi(
                state = state,
                title = "",
                onBackClicked = {},
                onSeasonClicked = { _, _ -> },
                onShowClicked = {},
                onWatchTrailerClicked = { _ -> },
                snackbarHostState = SnackbarHostState(),
                listState = LazyListState(),
                onAction = {},
            )
        }
    }
}