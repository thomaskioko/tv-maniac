package com.thomaskioko.tvmaniac.seasondetails.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.BasicDialog
import com.thomaskioko.tvmaniac.compose.components.CollapsableTopAppBar
import com.thomaskioko.tvmaniac.compose.components.EmptyContent
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.components.ExpandingText
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.SheetDragHandle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacBottomSheetScaffold
import com.thomaskioko.tvmaniac.compose.components.TvPosterCard
import com.thomaskioko.tvmaniac.compose.extensions.contentBackgroundGradient
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.presentation.seasondetails.DismissSeasonDialog
import com.thomaskioko.tvmaniac.presentation.seasondetails.DismissSeasonGallery
import com.thomaskioko.tvmaniac.presentation.seasondetails.InitialSeasonsState
import com.thomaskioko.tvmaniac.presentation.seasondetails.ReloadSeasonDetails
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailState
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsAction
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsBackClicked
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsComponent
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsErrorState
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsLoaded
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonGalleryClicked
import com.thomaskioko.tvmaniac.presentation.seasondetails.UpdateSeasonWatchedState
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.Cast
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonImagesModel
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.seasondetails.ui.components.CollapsableContent
import com.thomaskioko.tvmaniac.seasondetails.ui.components.ShowLinearProgressIndicator
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.collections.immutable.ImmutableList

@Composable
fun SeasonDetailsScreen(
  component: SeasonDetailsComponent,
  modifier: Modifier = Modifier,
) {
  val state by component.state.collectAsState()

  SeasonDetailsScreen(
    modifier = modifier,
    state = state,
    onAction = component::dispatch,
  )
}

@Composable
internal fun SeasonDetailsScreen(
  state: SeasonDetailState,
  modifier: Modifier = Modifier,
  onAction: (SeasonDetailsAction) -> Unit,
) {

  val listState = rememberLazyListState()

  TvManiacBottomSheetScaffold(
    modifier = modifier,
    showBottomSheet = state.showGalleryBottomSheet,
    sheetContent = { ImageGalleryContent(imageList = state.seasonImages) },
    onDismissBottomSheet = { onAction(DismissSeasonGallery) },
    sheetDragHandle = {
      val title =
        stringResource(
          id = R.string.cd_show_images,
          (state as? SeasonDetailsLoaded)?.seasonName ?: ""
        )
      SheetDragHandle(
        title = title,
        onClick = { onAction(DismissSeasonGallery) },
      )
    },
    content = { contentPadding ->
      Box(Modifier.fillMaxSize()) {
        when (state) {
          InitialSeasonsState ->
            EmptyContent(
              imageVector = Icons.Outlined.Autorenew,
              message = stringResource(id = R.string.generic_fetching_data),
            )
          is SeasonDetailsLoaded ->
            LazyColumnContent(
              seasonDetailsModel = state,
              isLoading = state.isUpdating,
              showSeasonWatchStateDialog = state.showSeasonWatchStateDialog,
              contentPadding = contentPadding,
              onAction = onAction,
              listState = listState,
            )
          is SeasonDetailsErrorState ->
            ErrorUi(
              errorIcon = {
                Image(
                  modifier = Modifier.size(120.dp),
                  imageVector = Icons.Outlined.ErrorOutline,
                  colorFilter =
                    ColorFilter.tint(MaterialTheme.colorScheme.secondary.copy(alpha = 0.8F)),
                  contentDescription = null,
                )
              },
              modifier = Modifier.fillMaxSize(),
              errorMessage = state.errorMessage,
              onRetry = { onAction(ReloadSeasonDetails) },
            )
        }

        CollapsableTopAppBar(
          listState = listState,
          title = (state as? SeasonDetailsLoaded)?.seasonName ?: "",
          isUpdating = state.isUpdating,
          onNavIconPressed = { onAction(SeasonDetailsBackClicked) },
          onActionIconPressed = { onAction(ReloadSeasonDetails) },
          showActionIcon = state is SeasonDetailsLoaded,
        )
      }
    },
  )
}

@Composable
fun LazyColumnContent(
  listState: LazyListState,
  isLoading: Boolean,
  showSeasonWatchStateDialog: Boolean,
  seasonDetailsModel: SeasonDetailsLoaded,
  contentPadding: PaddingValues,
  onAction: (SeasonDetailsAction) -> Unit,
  modifier: Modifier = Modifier,
) {

  val scrollState = rememberScrollState()

  LazyColumn(
    modifier = modifier,
    state = listState,
    contentPadding = contentPadding.copy(copyTop = false),
  ) {
    item {
      HeaderContent(
        scrollState = scrollState,
        imageUrl = seasonDetailsModel.imageUrl,
        title = seasonDetailsModel.seasonName,
        imagesCount = seasonDetailsModel.seasonImages.size,
        watchProgress = seasonDetailsModel.watchProgress,
        isLoading = isLoading,
        onAction = onAction,
        listState = listState,
      )
    }

    item {
      BodyContent(
        seasonDetailsModel = seasonDetailsModel,
        onAction = onAction,
      )
    }

    item { Spacer(modifier = Modifier.height(54.dp)) }
  }

  if (showSeasonWatchStateDialog) {
    SeasonsWatchDialog(
      isWatched = seasonDetailsModel.isSeasonWatched,
      onAction = onAction,
    )
  }
}

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
        modifier = Modifier.fillMaxWidth().animateItem(),
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
  listState: LazyListState,
  onAction: (SeasonDetailsAction) -> Unit,
) {
  val offset = (scrollState.value / 2)
  val offsetDp = with(LocalDensity.current) { offset.toDp() }
  val resources = LocalContext.current.resources

  Box(
    modifier =
      Modifier.fillMaxWidth().height(350.dp).clipToBounds().offset {
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
  seasonDetailsModel: SeasonDetailsLoaded,
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
  @PreviewParameter(SeasonPreviewParameterProvider::class) state: SeasonDetailState,
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
