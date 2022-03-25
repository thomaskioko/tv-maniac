package com.thomaskioko.tvmaniac.seasons

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.thomaskioko.tvmaniac.compose.components.NetworkImageComposable
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.seasonepisodes.api.model.Episode

@Composable
fun WatchlistRowItem(
    episode: Episode,
    onEpisodeClicked: (Long) -> Unit = {}
) {

    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .size(width = 260.dp, height = 84.dp)
            .clickable { onEpisodeClicked(episode.id) },
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {

            val (episodeTitle, image, overview, watchedStatusIcon) = createRefs()

            NetworkImageComposable(
                imageUrl = episode.imageUrl,
                contentDescription = stringResource(
                    R.string.cd_show_poster,
                    episode.episodeNumberTitle
                ),
                modifier = Modifier
                    .height(64.dp)
                    .width(84.dp)
                    .clip(MaterialTheme.shapes.medium)
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
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier
                    .constrainAs(episodeTitle) {
                        linkTo(
                            start = image.end,
                            end = watchedStatusIcon.start,
                            startMargin = 8.dp,
                            bias = 0f
                        )
                        top.linkTo(parent.top, 16.dp)

                        width = Dimension.preferredWrapContent
                    }
            )

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = episode.episodeTitle,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.overline,
                    modifier = Modifier
                        .constrainAs(overview) {
                            start.linkTo(image.end, 8.dp)
                            top.linkTo(episodeTitle.bottom, 5.dp)
                            end.linkTo(watchedStatusIcon.start, 8.dp)

                            width = Dimension.fillToConstraints
                        }
                )
            }

            IconButton(
                onClick = {},
                modifier = Modifier
                    .constrainAs(watchedStatusIcon) {
                        centerVerticallyTo(parent)
                        end.linkTo(parent.end, 8.dp)
                    }
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = stringResource(R.string.cd_navigate_back),
                    tint = LightGray,
                    modifier = Modifier
                        .size(32.dp)
                )
            }
        }
    }
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun WatchlistRowItemPreview() {
    TvManiacTheme {
        Surface {
            WatchlistRowItem(
                episode = episode
            )
        }
    }
}
