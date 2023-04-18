package com.thomaskioko.tvmaniac.videoplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.components.LoadingIndicator
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.domain.trailers.LoadingTrailers
import com.thomaskioko.tvmaniac.domain.trailers.ReloadTrailers
import com.thomaskioko.tvmaniac.domain.trailers.TrailerError
import com.thomaskioko.tvmaniac.domain.trailers.TrailerSelected
import com.thomaskioko.tvmaniac.domain.trailers.TrailersLoaded
import com.thomaskioko.tvmaniac.domain.trailers.TrailersState
import com.thomaskioko.tvmaniac.domain.trailers.VideoPlayerError
import com.thomaskioko.tvmaniac.domain.trailers.model.Trailer
import com.thomaskioko.tvmaniac.navigation.extensions.viewModel
import com.thomaskioko.tvmaniac.resources.R
import me.tatarka.inject.annotations.Inject


typealias Trailers = @Composable () -> Unit

@Inject
@Composable
fun Trailers(
    viewModelFactory: (SavedStateHandle) -> TrailersViewModel,
) {

    TrailersScreen(
        viewModel = viewModel(factory = viewModelFactory),
    )
}

@Composable
internal fun TrailersScreen(
    viewModel: TrailersViewModel,
    modifier: Modifier = Modifier,
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    TrailersScreen(
        modifier = modifier,
        state = viewState,
        onRetryClicked = { viewModel.dispatch(ReloadTrailers) },
        onYoutubeError = { viewModel.dispatch(VideoPlayerError(it)) },
        onTrailerClicked = { viewModel.dispatch(TrailerSelected(it)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrailersScreen(
    state: TrailersState,
    onRetryClicked: () -> Unit,
    onYoutubeError: (String) -> Unit,
    onTrailerClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    Scaffold(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        content = { contentPadding ->

            when (state) {
                is LoadingTrailers -> LoadingIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )

                is TrailersLoaded -> {
                    VideoPlayerContent(
                        listState = listState,
                        trailersList = state.trailersList,
                        videoKey = state.selectedVideoKey,
                        onYoutubeError = onYoutubeError,
                        onTrailerClicked = onTrailerClicked,
                        contentPadding = contentPadding,
                    )
                }

                is TrailerError -> ErrorUi(
                    errorMessage = state.errorMessage,
                    onRetry = onRetryClicked,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )
            }
        }
    )
}

@Composable
private fun VideoPlayerContent(
    listState: LazyListState,
    trailersList: List<Trailer>,
    videoKey: String,
    onYoutubeError: (String) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    onTrailerClicked: (String) -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                YouTubePlayerView(context).apply {
                    addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            youTubePlayer.loadVideo(
                                videoId = videoKey,
                                startSeconds = 0f
                            )
                        }

                        override fun onError(
                            youTubePlayer: YouTubePlayer,
                            error: PlayerConstants.PlayerError
                        ) {
                            super.onError(youTubePlayer, error)
                            onYoutubeError(error.name)
                        }
                    })
                }
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.str_more_trailers),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        TrailerList(
            listState = listState,
            trailerList = trailersList,
            onTrailerClicked = onTrailerClicked,
            contentPadding = contentPadding
        )
    }
}


@Composable
private fun TrailerList(
    listState: LazyListState,
    trailerList: List<Trailer>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    onTrailerClicked: (String) -> Unit = {}
) {

    LazyColumn(
        state = listState,
        contentPadding = contentPadding.copy(copyTop = false),
        modifier = modifier.fillMaxWidth()
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        items(trailerList) { trailer ->

            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clickable { onTrailerClicked(trailer.key) }
                    .padding(horizontal = 8.dp)
            ) {
                val (episodeTitle, image) = createRefs()

                AsyncImageComposable(
                    model = trailer.youtubeThumbnailUrl,
                    contentDescription = trailer.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(140.dp)
                        .drawWithCache {
                            onDrawWithContent {
                                drawContent()
                                drawRect(
                                    blendMode = BlendMode.Multiply,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black),
                                        startY = size.height / 3,
                                        endY = size.height
                                    )
                                )
                            }
                        }
                        .constrainAs(image) {
                            start.linkTo(parent.start)
                            bottom.linkTo(parent.bottom)
                            top.linkTo(parent.top)

                            height = Dimension.fillToConstraints
                        },
                )

                Text(
                    text = trailer.name,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .constrainAs(episodeTitle) {
                            linkTo(
                                start = image.end,
                                end = parent.end,
                                startMargin = 8.dp,
                                bias = 0f,
                            )
                            top.linkTo(parent.top)

                            width = Dimension.preferredWrapContent
                        }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

    }
}

@ThemePreviews
@Composable
private fun TrailerListContentPreview(
    @PreviewParameter(TrailerPreviewParameterProvider::class)
    state: TrailersState
) {
    TvManiacTheme {
        Surface {
            TrailersScreen(
                state = state,
                onRetryClicked = {},
                onTrailerClicked = {},
                onYoutubeError = {}
            )
        }
    }
}


