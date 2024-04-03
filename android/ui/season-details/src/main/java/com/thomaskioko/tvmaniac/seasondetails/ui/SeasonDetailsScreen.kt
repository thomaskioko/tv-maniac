package com.thomaskioko.tvmaniac.seasondetails.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.BasicDialog
import com.thomaskioko.tvmaniac.compose.components.ExpandingText
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacBottomSheetScaffold
import com.thomaskioko.tvmaniac.compose.components.TvPosterCard
import com.thomaskioko.tvmaniac.compose.extensions.contentBackgroundGradient
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.extensions.iconButtonBackgroundScrim
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.presentation.seasondetails.DismissSeasonDetailSnackBar
import com.thomaskioko.tvmaniac.presentation.seasondetails.DismissSeasonDialog
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsAction
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsBackClicked
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsContent
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsPresenter
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonGalleryClicked
import com.thomaskioko.tvmaniac.presentation.seasondetails.UpdateSeasonWatchedState
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.Cast
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonImagesModel
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.seasondetails.ui.components.CollapsableContent
import com.thomaskioko.tvmaniac.seasondetails.ui.components.ShowLinearProgressIndicator
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.collections.immutable.ImmutableList

@Composable
fun SeasonDetailsScreen(
  presenter: SeasonDetailsPresenter,
  modifier: Modifier = Modifier,
) {
  val state by presenter.value.subscribeAsState()

  SeasonDetailsScreen(
    modifier = modifier,
    state = state,
    onAction = presenter::dispatch,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SeasonDetailsScreen(
  state: SeasonDetailsContent,
  onAction: (SeasonDetailsAction) -> Unit,
  modifier: Modifier = Modifier,
) {
  TvManiacBottomSheetScaffold(
    modifier = modifier,
    showBottomSheet = state.showGalleryBottomSheet,
    onDismissBottomSheet = { onAction(SeasonGalleryClicked) },
    sheetContent = { ImageGalleryContent(imageList = state.seasonImages) },
    content = { contentPadding ->
      SeasonDetailsContent(
        seasonDetailsModel = state,
        isLoading = state.isLoading,
        showSeasonWatchStateDialog = state.showSeasonWatchStateDialog,
        contentPadding = contentPadding,
        onAction = onAction,
      )
    },
  )
}

@Composable
private fun SeasonDetailsContent(
  isLoading: Boolean,
  showSeasonWatchStateDialog: Boolean,
  seasonDetailsModel: SeasonDetailsContent,
  contentPadding: PaddingValues,
  onAction: (SeasonDetailsAction) -> Unit,
) {
  val scrollState = rememberScrollState()
  val snackBarHostState = remember { SnackbarHostState() }

  LaunchedEffect(key1 = seasonDetailsModel.errorMessage) {
    seasonDetailsModel.errorMessage?.let {
      val snackBarResult =
        snackBarHostState.showSnackbar(
          message = it,
          duration = SnackbarDuration.Short,
        )
      when (snackBarResult) {
        SnackbarResult.ActionPerformed,
        SnackbarResult.Dismissed, -> onAction(DismissSeasonDetailSnackBar)
      }
    }
  }

  Column(
    modifier = Modifier.padding(contentPadding.copy(copyBottom = false)).fillMaxSize(),
  ) {
    BoxWithConstraints(modifier = Modifier.weight(1f)) {
      Column(
        modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
      ) {
        if (showSeasonWatchStateDialog) {
          SeasonsWatchDialog(
            isWatched = seasonDetailsModel.isSeasonWatched,
            onAction = onAction,
          )
        }

        HeaderContent(
          scrollState = scrollState,
          imageUrl = seasonDetailsModel.imageUrl,
          title = seasonDetailsModel.seasonName,
          imagesCount = seasonDetailsModel.seasonImages.size,
          watchProgress = seasonDetailsModel.watchProgress,
          isLoading = isLoading,
          containerHeight = this@BoxWithConstraints.maxHeight,
          onAction = onAction,
        )

        BodyContent(
          seasonDetailsModel = seasonDetailsModel,
          onAction = onAction,
        )
      }
    }
  }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalSnapperApi::class)
@Composable
fun ImageGalleryContent(
  imageList: ImmutableList<SeasonImagesModel>,
  modifier: Modifier = Modifier,
) {
  val listState = rememberLazyListState()

  LazyVerticalStaggeredGrid(
    columns = StaggeredGridCells.Fixed(2),
    verticalItemSpacing = 4.dp,
    flingBehavior = rememberSnapperFlingBehavior(listState),
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    modifier = modifier.fillMaxSize(),
  ) {
    items(imageList) { item ->
      TvPosterCard(
        modifier = Modifier.fillMaxWidth().animateItemPlacement(),
        posterImageUrl = item.imageUrl,
        title = "",
        onClick = {},
        posterModifier = Modifier.fillMaxWidth().wrapContentHeight(),
      )
    }
  }
}

@Composable
private fun HeaderContent(
  scrollState: ScrollState,
  imageUrl: String?,
  title: String,
  watchProgress: Float,
  imagesCount: Int,
  isLoading: Boolean,
  containerHeight: Dp,
  onAction: (SeasonDetailsAction) -> Unit,
) {
  val offset = (scrollState.value / 2)
  val offsetDp = with(LocalDensity.current) { offset.toDp() }
  val resources = LocalContext.current.resources

  Box(
    modifier = Modifier.heightIn(max = containerHeight / 3),
    contentAlignment = Alignment.BottomCenter,
  ) {
    AsyncImageComposable(
      model = imageUrl,
      contentDescription = title,
      contentScale = ContentScale.Crop,
      modifier = Modifier.fillMaxWidth().padding(top = offsetDp),
    )

    Box(
      modifier = Modifier.matchParentSize().background(contentBackgroundGradient()),
    )
    IconButton(
      modifier =
        Modifier.align(Alignment.TopStart)
          .statusBarsPadding()
          .iconButtonBackgroundScrim(alpha = 0.7f),
      onClick = { onAction(SeasonDetailsBackClicked) },
    ) {
      Icon(
        imageVector = Icons.Default.ArrowBack,
        contentDescription = stringResource(R.string.cd_navigate_back),
        tint = MaterialTheme.colorScheme.onBackground,
      )
    }

    Row(
      modifier =
        Modifier.align(Alignment.BottomStart)
          .padding(horizontal = 16.dp, vertical = 32.dp)
          .clickable { onAction(SeasonGalleryClicked) },
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Icon(
        imageVector = Icons.Filled.PhotoLibrary,
        contentDescription = stringResource(R.string.cd_navigate_back),
        tint = MaterialTheme.colorScheme.onSurface,
      )

      Text(
        text =
          resources.getQuantityString(
            R.plurals.season_images_count,
            imagesCount,
            imagesCount,
          ),
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(horizontal = 8.dp),
      )
    }

    AnimatedVisibility(
      visible = isLoading,
      modifier = Modifier.align(Alignment.BottomEnd),
    ) {
      LoadingIndicator(
        modifier = Modifier.align(Alignment.BottomEnd).padding(32.dp).size(28.dp),
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    ShowLinearProgressIndicator(
      progress = watchProgress,
      modifier = Modifier.height(8.dp).fillMaxWidth(),
    )
  }
}

@Composable
private fun BodyContent(
  seasonDetailsModel: SeasonDetailsContent,
  onAction: (SeasonDetailsAction) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.fillMaxSize(),
  ) {
    Text(
      text = stringResource(R.string.title_season_overview),
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      style =
        MaterialTheme.typography.titleLarge.copy(
          color = MaterialTheme.colorScheme.onSurface,
          fontWeight = FontWeight.Medium,
        ),
    )

    ExpandingText(
      text = seasonDetailsModel.seasonOverview,
      textStyle = MaterialTheme.typography.bodyMedium,
      fontWeight = FontWeight.Normal,
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
    )

    Spacer(modifier = Modifier.height(8.dp))

    CollapsableContent(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
      episodesCount = seasonDetailsModel.episodeCount,
      watchProgress = seasonDetailsModel.watchProgress,
      isSeasonWatched = seasonDetailsModel.isSeasonWatched,
      episodeDetailsModelList = seasonDetailsModel.episodeDetailsList,
      collapsed = seasonDetailsModel.expandEpisodeItems,
      onAction = onAction,
    )

    CastContent(seasonDetailsModel.seasonCast)
  }
}

@Composable
@OptIn(ExperimentalSnapperApi::class, ExperimentalFoundationApi::class)
private fun CastContent(
  castList: ImmutableList<Cast>,
) {
  Column {
    Text(
      text = stringResource(R.string.title_casts),
      modifier = Modifier.padding(16.dp).fillMaxWidth(),
      style =
        MaterialTheme.typography.titleLarge.copy(
          color = MaterialTheme.colorScheme.onSurface,
          fontWeight = FontWeight.Medium,
        ),
    )

    Box(
      contentAlignment = Alignment.BottomCenter,
    ) {
      val lazyListState = rememberLazyListState()

      LazyRow(
        modifier = Modifier,
        state = lazyListState,
        flingBehavior = rememberSnapperFlingBehavior(lazyListState),
      ) {
        itemsIndexed(castList) { index, cast ->
          Card(
            modifier =
              Modifier.padding(
                start = if (index == 0) 16.dp else 0.dp,
                end = if (index == castList.size - 1) 16.dp else 8.dp,
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
                modifier = Modifier.fillMaxWidth().animateItemPlacement(),
              )

              Box(
                modifier = Modifier.matchParentSize().background(contentBackgroundGradient()),
              )
              Column(
                modifier = Modifier.padding(8.dp),
              ) {
                Text(
                  text = cast.name,
                  modifier = Modifier.padding(vertical = 4.dp).wrapContentWidth(),
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
private fun SeasonsWatchDialog(
  isWatched: Boolean,
  onAction: (SeasonDetailsAction) -> Unit,
) {
  val title =
    if (isWatched) {
      stringResource(id = R.string.dialog_title_unwatched)
    } else {
      stringResource(id = R.string.dialog_title_watched)
    }

  val message =
    if (isWatched) {
      stringResource(id = R.string.dialog_message_unwatched)
    } else {
      stringResource(id = R.string.dialog_message_watched)
    }

  BasicDialog(
    dialogTitle = title,
    dialogMessage = message,
    confirmButtonText = stringResource(id = R.string.dialog_button_yes),
    dismissButtonText = stringResource(id = R.string.dialog_button_no),
    onDismissDialog = { onAction(DismissSeasonDialog) },
    confirmButtonClicked = { onAction(UpdateSeasonWatchedState) },
    dismissButtonClicked = { onAction(DismissSeasonDialog) },
  )
}

@ThemePreviews
@Composable
private fun SeasonDetailScreenPreview(
  @PreviewParameter(SeasonPreviewParameterProvider::class) state: SeasonDetailsContent,
) {
  TvManiacTheme {
    Surface {
      SeasonDetailsScreen(
        state = state,
        onAction = {},
      )
    }
  }
}
