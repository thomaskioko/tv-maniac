package com.thomaskioko.tvmaniac.seasondetails.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.Episode
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.seasondetails.episode
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun WatchNextContent(
    episodeList: List<Episode>?,
    modifier: Modifier = Modifier,
) {
    episodeList?.let {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.title_watch_next),
                style = MaterialTheme.typography.labelMedium.copy(MaterialTheme.colorScheme.secondary),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val lazyListState = rememberLazyListState()

        LazyRow(
            state = lazyListState,
            flingBehavior = rememberSnapperFlingBehavior(lazyListState),
        ) {
            itemsIndexed(episodeList) { index, episode ->
                val value = if (index == 0) 32 else 8
                Spacer(modifier = Modifier.width(value.dp))

                WatchNextItem(
                    episode = episode,
                    onEpisodeClicked = {},
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun WatchNextItem(
    episode: Episode,
    modifier: Modifier = Modifier,
    onEpisodeClicked: (Long) -> Unit = {},
) {
    Card(
        shape = RectangleShape,
        modifier = modifier
            .size(width = 260.dp, height = 90.dp)
            .clickable { onEpisodeClicked(episode.id) },
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val (episodeTitle, image, overview, watchedStatusIcon) = createRefs()

            AsyncImageComposable(
                model = episode.imageUrl,
                contentDescription = stringResource(
                    R.string.cd_show_poster,
                    episode.episodeNumberTitle,
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(84.dp)
                    .constrainAs(image) {
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                        top.linkTo(parent.top)

                        height = Dimension.fillToConstraints
                    },
            )

            Text(
                text = episode.seasonEpisodeNumber,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .constrainAs(episodeTitle) {
                        linkTo(
                            start = image.end,
                            end = watchedStatusIcon.start,
                            startMargin = 8.dp,
                            bias = 0f,
                        )
                        top.linkTo(parent.top, 16.dp)

                        width = Dimension.preferredWrapContent
                    },
            )

            Text(
                text = episode.episodeTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .constrainAs(overview) {
                        start.linkTo(image.end, 8.dp)
                        top.linkTo(episodeTitle.bottom, 5.dp)
                        end.linkTo(watchedStatusIcon.start, 8.dp)
                        bottom.linkTo(parent.bottom)

                        width = Dimension.fillToConstraints
                    },
            )

            IconButton(
                onClick = {},
                modifier = Modifier
                    .constrainAs(watchedStatusIcon) {
                        centerVerticallyTo(parent)
                        end.linkTo(parent.end, 8.dp)
                    },
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(32.dp),
                )
            }
        }
    }
}

@ThemePreviews
@Composable
fun WatchlistRowItemPreview() {
    TvManiacTheme {
        Surface {
            WatchNextItem(
                episode = episode,
            )
        }
    }
}
