package com.thomaskioko.tvmaniac.ui.showdetails

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.LibraryAddCheck
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.CollapsableTopAppBar
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.components.ExpandingText
import com.thomaskioko.tvmaniac.compose.components.KenBurnsViewImage
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.TextLoadingItem
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacBottomSheetScaffold
import com.thomaskioko.tvmaniac.compose.components.TvManiacChip
import com.thomaskioko.tvmaniac.compose.components.TvManiacOutlinedButton
import com.thomaskioko.tvmaniac.compose.components.TvManiacTextButton
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.extensions.contentBackgroundGradient
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.theme.backgroundGradient
import com.thomaskioko.tvmaniac.presentation.showdetails.DetailBackClicked
import com.thomaskioko.tvmaniac.presentation.showdetails.DetailShowClicked
import com.thomaskioko.tvmaniac.presentation.showdetails.DismissErrorSnackbar
import com.thomaskioko.tvmaniac.presentation.showdetails.FollowShowClicked
import com.thomaskioko.tvmaniac.presentation.showdetails.ReloadShowDetails
import com.thomaskioko.tvmaniac.presentation.showdetails.SeasonClicked
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsAction
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsPresenter
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsContent
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowInfoState
import com.thomaskioko.tvmaniac.presentation.showdetails.WatchTrailerClicked
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Casts
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Providers
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Season
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Show
import com.thomaskioko.tvmaniac.presentation.showdetails.model.ShowDetails
import com.thomaskioko.tvmaniac.presentation.showdetails.model.ShowSeasonDetailsParam
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Trailer
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.ui.showdetails.DetailConstants.HEADER_HEIGHT
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ShowDetailsScreen(
  presenter: ShowDetailsPresenter,
  modifier: Modifier = Modifier,
) {
  val state by presenter.state.collectAsState()

  val snackBarHostState = remember { SnackbarHostState() }
  val listState = rememberLazyListState()

  ShowDetailsScreen(
    modifier = modifier,
    state = state,
    title = (state as? ShowDetailsContent)?.showDetails?.title ?: "",
    snackBarHostState = snackBarHostState,
    listState = listState,
    onAction = presenter::dispatch,
  )
}

@Composable
internal fun ShowDetailsScreen(
  state: ShowDetailsContent,
  title: String,
  snackBarHostState: SnackbarHostState,
  listState: LazyListState,
  onAction: (ShowDetailsAction) -> Unit,
  modifier: Modifier = Modifier,
) {
  TvManiacBottomSheetScaffold(
    modifier = modifier,
    showBottomSheet = false,
    onDismissBottomSheet = {},
    sheetContent = {
      // TODO: Add Trailer content
    },
    snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
    content = { contentPadding ->
      LaunchedEffect(key1 = state.errorMessage) {
        if (state.errorMessage != null) {
          val actionResult =
            snackBarHostState.showSnackbar(
              message = state.errorMessage!!,
              actionLabel = "Dismiss",
              withDismissAction = false,
              duration = SnackbarDuration.Short,
            )
          when (actionResult) {
            SnackbarResult.ActionPerformed,
            SnackbarResult.Dismissed, -> {
              onAction(DismissErrorSnackbar)
            }
          }
        }
      }

      Box(Modifier.fillMaxSize()) {
        if (state.showDetails != null) {
          LazyColumnContent(
            detailsContent = state,
            contentPadding = contentPadding,
            listState = listState,
            onAction = onAction,
          )
        } else if (!state.isUpdating && state.errorMessage != null) {
          ErrorUi(
            modifier = Modifier.fillMaxSize().padding(top = 16.dp),
            errorMessage = stringResource(R.string.generic_error_message),
            onRetry = { onAction(ReloadShowDetails) },
            errorIcon = {
              Image(
                modifier = Modifier.size(120.dp),
                imageVector = Icons.Outlined.ErrorOutline,
                colorFilter =
                  ColorFilter.tint(MaterialTheme.colorScheme.secondary.copy(alpha = 0.8F)),
                contentDescription = null,
              )
            },
          )
        }

        CollapsableTopAppBar(
          listState = listState,
          title = title,
          isUpdating = state.isUpdating || state.showInfo is ShowInfoState.Loading,
          showActionIcon = state.showInfo != ShowInfoState.Empty,
          onNavIconPressed = { onAction(DetailBackClicked) },
          onActionIconPressed = { onAction(ReloadShowDetails) },
        )
      }
    },
  )
}

@Composable
fun LazyColumnContent(
  detailsContent: ShowDetailsContent,
  listState: LazyListState,
  contentPadding: PaddingValues,
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
        show = detailsContent.showDetails,
        onUpdateFavoriteClicked = { onAction(FollowShowClicked(it)) },
        onWatchTrailerClicked = { onAction(WatchTrailerClicked(it)) },
      )
    }

    item {
      ShowInfoContent(
        showInfoState = detailsContent.showInfo,
        onAction = onAction,
      )
    }
  }
}

@Composable
private fun EmptyInfoContent(
  onAction: (ShowDetailsAction) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column {
    ErrorUi(
      modifier = modifier.fillMaxWidth().padding(top = 16.dp),
      errorMessage = stringResource(R.string.generic_error_message),
      onRetry = { onAction(ReloadShowDetails) },
    )

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
private fun ShowInfoContent(
  showInfoState: ShowInfoState,
  onAction: (ShowDetailsAction) -> Unit,
  modifier: Modifier = Modifier,
) {
  when (showInfoState) {
    ShowInfoState.Empty,
    ShowInfoState.Loading ->
      LoadingIndicator(
        modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
      )
    is ShowInfoState.Error ->
      EmptyInfoContent(
        modifier = Modifier.fillMaxSize(),
        onAction = { onAction(ReloadShowDetails) }
      )
    is ShowInfoState.Loaded -> {
      Column(modifier = modifier.fillMaxWidth()) {
        SeasonsContent(
          seasonsList = showInfoState.seasonsList,
          selectedSeasonIndex = showInfoState.selectedSeasonIndex,
          onAction = onAction,
        )

        WatchProvider(list = showInfoState.providers)

        TrailersContent(
          trailersList = showInfoState.trailersList,
          onAction = onAction,
        )

        CastContent(castsList = showInfoState.castsList)

        RecommendedShowsContent(
          recommendedShows = showInfoState.recommendedShowList,
          onShowClicked = { onAction(DetailShowClicked(it)) },
        )

        SimilarShowsContent(
          similarShows = showInfoState.similarShows,
          onShowClicked = { onAction(DetailShowClicked(it)) },
        )

        Spacer(modifier = Modifier.height(54.dp))
      }
    }
  }
}

@Composable
private fun HeaderContent(
  show: ShowDetails?,
  listState: LazyListState,
  onUpdateFavoriteClicked: (Boolean) -> Unit,
  onWatchTrailerClicked: (Long) -> Unit,
) {
  Box(
    modifier =
      Modifier.fillMaxWidth().height(HEADER_HEIGHT).clipToBounds().offset {
        IntOffset(
          x = 0,
          y =
            if (listState.firstVisibleItemIndex == 0) {
              listState.firstVisibleItemScrollOffset / 2
            } else {
              0
            },
        )
      },
  ) {
    KenBurnsViewImage(
      imageUrl = show?.backdropImageUrl,
      modifier = Modifier.fillMaxSize().clipToBounds(),
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
  show: ShowDetails,
  onUpdateFavoriteClicked: (Boolean) -> Unit,
  onWatchTrailerClicked: (Long) -> Unit,
) {
  val surfaceGradient = backgroundGradient().reversed()

  Box(
    modifier =
      Modifier.fillMaxSize()
        .clipToBounds()
        .background(Brush.verticalGradient(surfaceGradient))
        .padding(horizontal = 16.dp),
  ) {
    Spacer(modifier = Modifier.height(16.dp))

    Column(
      modifier = Modifier.fillMaxSize(),
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
        id = show.tmdbId,
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
      val tagStyle =
        MaterialTheme.typography.labelMedium
          .toSpanStyle()
          .copy(
            color = MaterialTheme.colorScheme.secondary,
          )
      withStyle(tagStyle) { append("  •  ") }
    }
    val text = buildAnnotatedString {
      val statusStyle =
        MaterialTheme.typography.labelMedium
          .toSpanStyle()
          .copy(
            color = MaterialTheme.colorScheme.secondary,
            background = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
          )

      val tagStyle =
        MaterialTheme.typography.labelMedium
          .toSpanStyle()
          .copy(
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

      withStyle(tagStyle) { append(releaseYear) }

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
        withStyle(tagStyle) { append(language) }
        append(divider)
      }
      withStyle(tagStyle) { append("$rating") }
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
        buttonColors =
          ButtonDefaults.buttonColors(
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
  id: Long,
  onUpdateFavoriteClicked: (Boolean) -> Unit,
  onWatchTrailerClicked: (Long) -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.Center,
  ) {
    TvManiacOutlinedButton(
      modifier = Modifier.fillMaxWidth().weight(1f),
      leadingIcon = {
        Image(
          imageVector =
            if (isFollowed) {
              Icons.Filled.LibraryAddCheck
            } else {
              Icons.Filled.LibraryAdd
            },
          contentDescription = null,
          colorFilter =
            ColorFilter.tint(
              MaterialTheme.colorScheme.secondary.copy(
                alpha = 0.8F,
              ),
            ),
        )
      },
      text =
        if (isFollowed) {
          stringResource(id = R.string.unfollow)
        } else {
          stringResource(id = R.string.following)
        },
      textPadding = 8.dp,
      onClick = { onUpdateFavoriteClicked(isFollowed) },
      borderColor = MaterialTheme.colorScheme.secondary,
    )

    Spacer(modifier = Modifier.width(8.dp))

    TvManiacOutlinedButton(
      modifier = Modifier.fillMaxWidth().weight(1f),
      leadingIcon = {
        Image(
          imageVector = Icons.Filled.Movie,
          contentDescription = null,
          colorFilter =
            ColorFilter.tint(
              MaterialTheme.colorScheme.secondary.copy(
                alpha = 0.8F,
              ),
            ),
        )
      },
      text = stringResource(id = R.string.btn_trailer),
      textPadding = 8.dp,
      borderColor = MaterialTheme.colorScheme.secondary,
      onClick = { onWatchTrailerClicked(id) },
    )
  }
}

@Composable
private fun SeasonsContent(
  seasonsList: ImmutableList<Season>,
  selectedSeasonIndex: Int,
  onAction: (ShowDetailsAction) -> Unit,
) {
  if (seasonsList.isEmpty()) return

  Spacer(modifier = Modifier.height(16.dp))

  TextLoadingItem(
    title = stringResource(id = R.string.title_seasons),
  ) {
    val selectedIndex by remember { mutableIntStateOf(selectedSeasonIndex) }

    ScrollableTabRow(
      selectedTabIndex = selectedIndex,
      divider = {}, /* Disable the built-in divider */
      indicator = {},
      edgePadding = 0.dp,
      containerColor = Color.Transparent,
      modifier = Modifier.fillMaxWidth(),
    ) {
      seasonsList.forEachIndexed { index, season ->
        val value = if (index == 0) 16 else 4
        Tab(
          modifier = Modifier.padding(start = value.dp, end = 4.dp),
          selected = index == selectedIndex,
          onClick = {},
        ) {
          TvManiacChip(
            text = season.name,
            onClick = {
              onAction(
                SeasonClicked(
                  ShowSeasonDetailsParam(
                    season.tvShowId,
                    season.seasonId,
                    season.seasonNumber,
                    selectedSeasonIndex = index,
                  ),
                ),
              )
            },
          )
        }
      }
    }
  }
}

@Composable
fun WatchProvider(
  list: ImmutableList<Providers>,
  modifier: Modifier = Modifier,
) {
  if (list.isEmpty()) return

  Spacer(modifier = Modifier.height(8.dp))

  TextLoadingItem(
    title = stringResource(R.string.title_providers),
    subTitle = stringResource(R.string.title_providers_label),
  ) {
    val lazyListState = rememberLazyListState()

    LazyRow(
      modifier = modifier,
      state = lazyListState,
      flingBehavior = rememberSnapperFlingBehavior(lazyListState),
    ) {
      itemsIndexed(list) { index, tvShow ->
        val value = if (index == 0) 16 else 4

        Spacer(modifier = Modifier.width(value.dp))

        Card(
          modifier =
            Modifier.size(width = 80.dp, height = 60.dp)
              .padding(
                end = if (index == list.size - 1) 16.dp else 8.dp,
              ),
          shape = MaterialTheme.shapes.small,
          elevation =
            CardDefaults.cardElevation(
              defaultElevation = 8.dp,
            ),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ) {
          AsyncImageComposable(
            model = tvShow.logoUrl,
            contentDescription = tvShow.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().aspectRatio(1f).animateItem(),
          )
        }
      }
    }
  }
}

@Composable
private fun CastContent(
  castsList: ImmutableList<Casts>,
) {
  if (castsList.isEmpty()) return

  TextLoadingItem(
    title = stringResource(R.string.title_casts),
  ) {
    Box(
      contentAlignment = Alignment.BottomCenter,
    ) {
      val lazyListState = rememberLazyListState()

      LazyRow(
        modifier = Modifier,
        state = lazyListState,
        flingBehavior = rememberSnapperFlingBehavior(lazyListState),
      ) {
        itemsIndexed(castsList) { index, cast ->
          Card(
            modifier =
              Modifier.padding(
                start = if (index == 0) 16.dp else 0.dp,
                end = if (index == castsList.size - 1) 16.dp else 8.dp,
              ),
            shape = MaterialTheme.shapes.small,
            elevation =
              CardDefaults.cardElevation(
                defaultElevation = 8.dp,
              ),
          ) {
            Box(
              modifier = Modifier.fillMaxSize().size(width = 120.dp, height = 160.dp),
              contentAlignment = Alignment.BottomStart,
            ) {
              AsyncImageComposable(
                model = cast.profileUrl,
                contentDescription = cast.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().animateItem(),
              )

              Box(
                modifier = Modifier.matchParentSize().background(contentBackgroundGradient()),
              )
              Column(
                modifier = Modifier.padding(8.dp),
              ) {
                Text(
                  text = cast.name,
                  modifier = Modifier.padding(vertical = 2.dp).wrapContentWidth(),
                  overflow = TextOverflow.Ellipsis,
                  maxLines = 1,
                  style =
                    MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface,
                    ),
                )

                Text(
                  text = cast.characterName,
                  modifier = Modifier.wrapContentWidth(),
                  overflow = TextOverflow.Ellipsis,
                  maxLines = 1,
                  style =
                    MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = FontWeight.Normal,
                      color = MaterialTheme.colorScheme.onSurface,
                    ),
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun TrailersContent(
  trailersList: ImmutableList<Trailer>,
  onAction: (ShowDetailsAction) -> Unit,
) {
  if (trailersList.isEmpty()) return

  Spacer(modifier = Modifier.height(16.dp))

  TextLoadingItem(
    title = stringResource(id = R.string.title_trailer),
  ) {
    val lazyListState = rememberLazyListState()

    LazyRow(
      state = lazyListState,
      flingBehavior = rememberSnapperFlingBehavior(lazyListState),
    ) {
      itemsIndexed(trailersList) { index, trailer ->
        val value = if (index == 0) 16 else 8
        Spacer(modifier = Modifier.width(value.dp))

        Column {
          Card(
            modifier = Modifier.clickable { onAction(WatchTrailerClicked(trailer.showId)) },
            shape = RoundedCornerShape(4.dp),
            elevation =
              CardDefaults.cardElevation(
                defaultElevation = 4.dp,
              ),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
          ) {
            Box {
              AsyncImageComposable(
                model = trailer.youtubeThumbnailUrl,
                contentDescription = trailer.name,
                contentScale = ContentScale.Crop,
                modifier =
                  Modifier.height(140.dp).aspectRatio(3 / 1.5f).drawWithCache {
                    val gradient =
                      Brush.verticalGradient(
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
                tint = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.align(Alignment.Center).size(48.dp),
              )
            }
          }

          Text(
            text = trailer.name,
            modifier = Modifier.padding(vertical = 8.dp).widthIn(0.dp, 280.dp),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style =
              MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Normal,
              ),
          )
        }
      }
    }
  }
}

@Composable
fun RecommendedShowsContent(
  recommendedShows: ImmutableList<Show>,
  modifier: Modifier = Modifier,
  onShowClicked: (Long) -> Unit = {},
) {
  if (recommendedShows.isEmpty()) return

  Spacer(modifier = Modifier.height(16.dp))

  val lazyListState = rememberLazyListState()
  TextLoadingItem(
    title = stringResource(id = R.string.title_recommended),
  ) {
    LazyRow(
      modifier = modifier,
      state = lazyListState,
      flingBehavior = rememberSnapperFlingBehavior(lazyListState),
    ) {
      itemsIndexed(recommendedShows) { index, tvShow ->
        val value = if (index == 0) 16 else 4

        Spacer(modifier = Modifier.width(value.dp))

        PosterCard(
          imageUrl = tvShow.posterImageUrl,
          title = tvShow.title,
          onClick = { onShowClicked(tvShow.tmdbId) },
          imageWidth = 84.dp,
        )
      }
    }
  }
}

@Composable
fun SimilarShowsContent(
  similarShows: ImmutableList<Show>,
  modifier: Modifier = Modifier,
  onShowClicked: (Long) -> Unit = {},
) {
  if (similarShows.isEmpty()) return

  Spacer(modifier = Modifier.height(16.dp))

  val lazyListState = rememberLazyListState()

  TextLoadingItem(
    title = stringResource(id = R.string.title_similar),
  ) {
    LazyRow(
      modifier = modifier,
      state = lazyListState,
      flingBehavior = rememberSnapperFlingBehavior(lazyListState),
    ) {
      itemsIndexed(similarShows) { index, tvShow ->
        val value = if (index == 0) 16 else 4

        Spacer(modifier = Modifier.width(value.dp))

        PosterCard(
          imageUrl = tvShow.posterImageUrl,
          title = tvShow.title,
          onClick = { onShowClicked(tvShow.tmdbId) },
          imageWidth = 84.dp,
        )
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
  @PreviewParameter(DetailPreviewParameterProvider::class) state: ShowDetailsContent,
) {
  TvManiacTheme {
    Surface {
      ShowDetailsScreen(
        state = state,
        title = "",
        snackBarHostState = SnackbarHostState(),
        listState = LazyListState(),
        onAction = {},
      )
    }
  }
}
