package com.thomaskioko.tvmaniac.ui.detail.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.thomaskioko.tvmaniac.R
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.components.NetworkImageComposable
import com.thomaskioko.tvmaniac.presentation.model.Episode

@Composable
fun EpisodesScreen(
    seasonEpisodes: List<Episode>,
) {
    Column (
        modifier = Modifier.fillMaxWidth()
            ){
        seasonEpisodes.forEach { episode ->
            EpisodeItem(episode)
            ColumnSpacer(16)
        }

    }
}

@Composable
fun EpisodeItem(episode: Episode) {
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
    ) {

        val (divider, episodeTitle, image, overview, episodeNumber) = createRefs()

        Divider(
            Modifier
                .padding(start = 16.dp, end = 16.dp)
                .constrainAs(divider) {
                    top.linkTo(parent.top)
                    centerHorizontallyTo(parent)

                    width = Dimension.fillToConstraints
                }
        )

        Card(
            elevation = 4.dp,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .width(64.dp)
                .height(74.dp)
                .constrainAs(image) {
                    start.linkTo(parent.start, 16.dp)
                    top.linkTo(parent.top, margin = 16.dp)
                    bottom.linkTo(parent.bottom, 8.dp)

                }
        ) {
            NetworkImageComposable(
                imageUrl = episode.imageUrl,
                contentDescription = stringResource(R.string.cd_show_poster, episode.name),
                modifier = Modifier
                    .width(64.dp)
                    .height(84.dp)
                    .clip(MaterialTheme.shapes.medium),
            )
        }

        Text(
            text = episode.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier
                .padding(top = 8.dp)
                .constrainAs(episodeTitle) {
                    linkTo(
                        start = image.end,
                        end = episodeNumber.start,
                        startMargin = 8.dp,
                        endMargin = 16.dp,
                        bias = 0f
                    )
                    top.linkTo(parent.top, 16.dp)

                    width = Dimension.preferredWrapContent
                }
        )

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = episode.overview,
                style = MaterialTheme.typography.body2,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .constrainAs(overview) {
                        linkTo(
                            start = image.end,
                            startMargin = 8.dp,
                            end = episodeNumber.start,
                            endMargin = 8.dp,
                            bias = 0f // float this towards the start
                        )

                        top.linkTo(episodeTitle.bottom, 5.dp)
                        end.linkTo(episodeNumber.start)

                        width = Dimension.preferredWrapContent
                    }
            )
        }

        Text(
            text = episode.episodeNumber,
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .padding(start = 4.dp)
                .constrainAs(episodeNumber) {
                    centerVerticallyTo(parent)
                    end.linkTo(parent.end, 16.dp)
                }
        )
    }
}
