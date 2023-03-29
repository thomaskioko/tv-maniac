package com.thomaskioko.tvmaniac.videoplayer

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.CircularLoadingView
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.components.ErrorUi
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.data.trailers.LoadingTrailers
import com.thomaskioko.tvmaniac.data.trailers.ReloadTrailers
import com.thomaskioko.tvmaniac.data.trailers.TrailerError
import com.thomaskioko.tvmaniac.data.trailers.TrailerSelected
import com.thomaskioko.tvmaniac.data.trailers.TrailersLoaded
import com.thomaskioko.tvmaniac.data.trailers.VideoPlayerError
import com.thomaskioko.tvmaniac.data.trailers.model.Trailer
import com.thomaskioko.tvmaniac.resources.R

@Composable
fun VideoPlayerScreen(
    viewModel: VideoPlayerViewModel,
) {

    val listState = rememberLazyListState()
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    when (viewState) {
        is LoadingTrailers -> CircularLoadingView()
        is TrailersLoaded -> {
            val state = (viewState as TrailersLoaded)
            Content(
                listState = listState,
                trailersList = state.trailersList,
                videoKey = state.selectedVideoKey,
                onYoutubeError = { viewModel.dispatch(VideoPlayerError(it)) },
                onTrailerClicked = { viewModel.dispatch(TrailerSelected(it)) }
            )
        }

        is TrailerError -> ErrorUi(
            errorMessage = (viewState as TrailerError).errorMessage,
            onRetry = { viewModel.dispatch(ReloadTrailers) }
        )
    }
}

@Composable
private fun Content(
    listState: LazyListState,
    trailersList: List<Trailer>,
    videoKey: String,
    onYoutubeError: (String) -> Unit,
    onTrailerClicked: (String) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        AndroidView(
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

        ColumnSpacer(16)

        Text(
            text = stringResource(id = R.string.str_more_trailers),
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        ColumnSpacer(8)

        TrailerList(
            listState = listState,
            trailerList = trailersList,
            onTrailerClicked = onTrailerClicked
        )
    }
}


@Composable
private fun TrailerList(
    listState: LazyListState,
    trailerList: List<Trailer>,
    onTrailerClicked: (String) -> Unit = {}
) {

    ColumnSpacer(8)

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxWidth()
    ) {
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
                            val gradient = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black),
                                startY = size.height / 3,
                                endY = size.height
                            )
                            onDrawWithContent {
                                drawContent()
                                drawRect(gradient, blendMode = BlendMode.Multiply)
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
                    style = MaterialTheme.typography.subtitle2,
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



            ColumnSpacer(8)
        }

    }
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TrailerListContentPreview() {
    TvManiacTheme {
        Surface {
            TrailerList(
                listState = LazyListState(),
                trailerList = listOf(
                    Trailer(
                        showId = 123,
                        key = "23aas",
                        name = "The Sandman Official Trailer",
                        youtubeThumbnailUrl = "someUrl"
                    ),
                    Trailer(
                        showId = 123,
                        key = "23aas",
                        name = "Inside Game of Thrones: A Story In The Camera Works",
                        youtubeThumbnailUrl = "someUrl"
                    )
                )
            )
        }
    }
}


