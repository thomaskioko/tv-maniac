package com.thomaskioko.tvmaniac.seasondetails.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.presentation.seasondetails.UpdateEpisodeStatus
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.seasondetails.episode

@Composable
fun EpisodeItem(
    imageUrl: String?,
    title: String,
    episodeOverview: String,
    onAction: (UpdateEpisodeStatus) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    onEpisodeClicked: () -> Unit = {},
) {
    Card(
        shape = shape,
        modifier = modifier
            .clickable { onEpisodeClicked() },
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val (episodeTitle, image, overview, watchedStatusIcon) = createRefs()

            AsyncImageComposable(
                model = imageUrl,
                contentDescription = stringResource(
                    R.string.cd_show_poster,
                    title,
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(94.dp)
                    .constrainAs(image) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)

                        height = Dimension.fillToConstraints
                    },
            )

            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .constrainAs(episodeTitle) {
                        start.linkTo(image.end, 8.dp)
                        end.linkTo(watchedStatusIcon.start)
                        top.linkTo(parent.top, 8.dp)

                        width = Dimension.fillToConstraints
                    },
            )

            Text(
                text = episodeOverview,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .constrainAs(overview) {
                        start.linkTo(image.end, 8.dp)
                        top.linkTo(episodeTitle.bottom, 5.dp)
                        end.linkTo(watchedStatusIcon.start, 8.dp)
                        bottom.linkTo(parent.bottom, 8.dp)

                        width = Dimension.fillToConstraints
                    },
            )

            IconButton(
                onClick = { onAction(UpdateEpisodeStatus(episode.id)) },
                modifier = Modifier
                    .constrainAs(watchedStatusIcon) {
                        centerVerticallyTo(parent)
                        end.linkTo(parent.end, 8.dp)
                    },
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = stringResource(R.string.cd_navigate_back),
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
            EpisodeItem(
                title = episode.episodeNumberTitle,
                episodeOverview = episode.overview,
                imageUrl = episode.imageUrl,
                onAction = {},
            )
        }
    }
}
