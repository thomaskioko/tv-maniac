package com.thomaskioko.showdetails

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.components.RowSpacer
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastAirEpisode
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun EpisodesReleaseContent(
    episodeList: List<LastAirEpisode>,
    onEpisodeClicked: (Long, Long) -> Unit = { _, _ -> },
    onBookmarkEpClicked: (Long) -> Unit = { }
) {

    ColumnSpacer(8)

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Text(
            text = "Episodes",
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .fillMaxWidth()
        )

        ColumnSpacer(8)

        val lazyListState = rememberLazyListState()

        LazyRow(
            state = lazyListState,
            flingBehavior = rememberSnapperFlingBehavior(lazyListState),
        ) {
            items(episodeList) { episode ->

                if (!episode.name.isNullOrEmpty()) {
                    EpisodeItem(
                        episode = episode,
                        onEpisodeClicked = onEpisodeClicked,
                        onBookmarkEpClicked = onBookmarkEpClicked
                    )
                }

                RowSpacer(8)
            }
        }
    }
}

@Composable
fun EpisodeItem(
    episode: LastAirEpisode,
    onEpisodeClicked: (Long, Long) -> Unit = { _, _ -> },
    onBookmarkEpClicked: (Long) -> Unit = { }
) {

    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .size(width = 320.dp, height = 200.dp)
            .padding(start = 2.dp, top = 2.dp, bottom = 16.dp)
            .clickable {
                onEpisodeClicked(episode.seasonNumber, episode.episodeNumber)
            },
    ) {
        ConstraintLayout {

            val (episodeRating, episodeTitle, releaseDate, image, plusIcon, overview) = createRefs()

            Image(
                imageVector = Icons.Filled.Bookmark,
                colorFilter = ColorFilter.tint(Color.Gray.copy(alpha = 0.8F)),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .constrainAs(image) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top, 8.dp)
                    }
                    .clickable { onBookmarkEpClicked(episode.id) }
            )

            Image(
                imageVector = Icons.Filled.Add,
                colorFilter = ColorFilter.tint(Color.Black),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .constrainAs(plusIcon) {
                        start.linkTo(image.start, 8.dp)
                        top.linkTo(image.top)
                        end.linkTo(image.end, 8.dp)
                        bottom.linkTo(image.bottom, 8.dp)
                    }
            )

            Text(
                text = episode.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier
                    .constrainAs(episodeRating) {
                        start.linkTo(image.end, 8.dp)
                        top.linkTo(parent.top, 16.dp)

                        width = Dimension.fillToConstraints
                    }
                    .background(
                        color = MaterialTheme.colors.secondary,
                        shape = CutCornerShape(
                            topEnd = 1.dp,
                            topStart = 2.dp,
                            bottomStart = 2.dp,
                            bottomEnd = 25.dp
                        )
                    )
                    .padding(
                        top = 4.dp,
                        bottom = 2.dp,
                        start = 16.dp,
                        end = 54.dp
                    )
            )

            Text(
                text = episode.airDate,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .constrainAs(releaseDate) {
                        top.linkTo(episodeRating.bottom, 8.dp)
                        start.linkTo(image.end, 8.dp)

                        width = Dimension.preferredWrapContent
                    }
            )

            Text(
                text = episode.name!!,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier
                    .constrainAs(episodeTitle) {
                        start.linkTo(parent.start, 8.dp)
                        top.linkTo(image.bottom, 5.dp)
                        end.linkTo(parent.end, 8.dp)

                        width = Dimension.fillToConstraints
                    }
            )

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = episode.overview,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .constrainAs(overview) {
                            start.linkTo(parent.start, 8.dp)
                            top.linkTo(episodeTitle.bottom, 5.dp)
                            end.linkTo(parent.end, 8.dp)

                            width = Dimension.fillToConstraints
                        }
                )
            }
        }
    }
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun EpisodesReleaseContentPreview() {
    TvManiacTheme {
        Surface {
            EpisodesReleaseContent(
                episodeList = detailUiState.lastAirEpList
            )
        }
    }
}
