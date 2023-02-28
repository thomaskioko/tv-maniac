package com.thomaskioko.tvmaniac.seasondetails.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.data.seasondetails.model.Episode
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.seasondetails.episode

@Composable
fun EpisodeListItem(
    episode: Episode,
    onEpisodeClicked: (Long) -> Unit = {}
) {

    ColumnSpacer(8)

    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onEpisodeClicked(episode.id) },
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {

            val (episodeTitle, image, overview, watchedStatusIcon) = createRefs()

            AsyncImageComposable(
                model = episode.imageUrl,
                contentDescription = stringResource(
                    R.string.cd_show_poster,
                    episode.episodeNumberTitle
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(84.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .constrainAs(image) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)

                        height = Dimension.fillToConstraints
                    },
            )

            Text(
                text = episode.episodeNumberTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier
                    .constrainAs(episodeTitle) {
                        start.linkTo(image.end, 8.dp)
                        end.linkTo(watchedStatusIcon.start)
                        top.linkTo(parent.top, 8.dp)

                        width = Dimension.fillToConstraints
                    }
            )

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = episode.overview,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier
                        .constrainAs(overview) {
                            start.linkTo(image.end, 8.dp)
                            top.linkTo(episodeTitle.bottom, 5.dp)
                            end.linkTo(watchedStatusIcon.start, 8.dp)
                            bottom.linkTo(parent.bottom, 8.dp)

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
                    tint = Color.LightGray,
                    modifier = Modifier
                        .size(32.dp)
                )
            }
        }
    }

    ColumnSpacer(8)
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun EpisodesScreenPreview() {
    TvManiacTheme {
        Surface {
            EpisodeListItem(
                episode = episode
            )
        }
    }
}
