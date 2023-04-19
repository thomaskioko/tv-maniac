package com.thomaskioko.tvmaniac.seasondetails.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.RectangleShape
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
import com.thomaskioko.tvmaniac.compose.theme.green
import com.thomaskioko.tvmaniac.domain.seasondetails.model.Episode
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.seasondetails.episode
import com.thomaskioko.tvmaniac.seasondetails.seasonDetails

@Composable
fun CollapsableContent(
    headerTitle: String,
    episodesCount: Long,
    watchProgress: Float,
    episodeList: List<Episode>,
    collapsed: Boolean,
    modifier: Modifier = Modifier,
    onEpisodeClicked: (Long) -> Unit = {},
    onSeasonHeaderClicked: () -> Unit = {},
) {
    Column(
        modifier = modifier,
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        SeasonTitleHeader(
            title = headerTitle,
            episodesCount = episodesCount,
            watchProgress = watchProgress,
            expanded = !collapsed,
            onSeasonHeaderClicked = onSeasonHeaderClicked,
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (!collapsed) {
            episodeList.forEach { episode ->
                Spacer(modifier = Modifier.height(8.dp))

                EpisodeItem(
                    episode = episode,
                    onEpisodeClicked = onEpisodeClicked,
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
private fun SeasonTitleHeader(
    title: String,
    episodesCount: Long,
    watchProgress: Float,
    expanded: Boolean,
    onSeasonHeaderClicked: () -> Unit = {},
) {
    val transitionState = remember {
        MutableTransitionState(expanded).apply {
            targetState = !expanded
        }
    }

    val transition = updateTransition(transitionState, label = "transition")
    val arrowRotationDegree by transition.animateFloat(
        label = "rotationDegreeTransition",
        transitionSpec = { tween(durationMillis = EXPANSION_TRANSITION_DURATION) },
        targetValueByState = { if (expanded) 0f else 180f },
    )

    Card(
        shape = RectangleShape,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp)
            .clickable { onSeasonHeaderClicked() },
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            val (episodeTitle, image, count, watchedStatusIcon, watchlistProgress) = createRefs()

            Icon(
                imageVector = Icons.Rounded.ExpandLess,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .rotate(arrowRotationDegree)
                    .constrainAs(image) {
                        start.linkTo(parent.start, 8.dp)
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
                        end.linkTo(count.start)
                        top.linkTo(image.top)
                        bottom.linkTo(image.bottom)

                        width = Dimension.fillToConstraints
                    },
            )

            Text(
                text = "$episodesCount",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .constrainAs(count) {
                        end.linkTo(watchedStatusIcon.start, 8.dp)
                        top.linkTo(watchedStatusIcon.top)
                        bottom.linkTo(watchedStatusIcon.bottom)

                        width = Dimension.preferredWrapContent
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
                    tint = if (watchProgress == 1f) green else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(28.dp),
                )
            }

            LinearProgressIndicator(
                progress = watchProgress,
                color = if (watchProgress == 1f) green else MaterialTheme.colorScheme.secondary,
                trackColor = LightGray,
                modifier = Modifier
                    .height(8.dp)
                    .constrainAs(watchlistProgress) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(image.bottom)
                        bottom.linkTo(parent.bottom, 12.dp)

                        width = Dimension.fillToConstraints
                    },
            )
        }
    }
}

@Composable
fun EpisodeItem(
    episode: Episode,
    modifier: Modifier = Modifier,
    onEpisodeClicked: (Long) -> Unit = {},
) {
    Card(
        shape = RectangleShape,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 84.dp)
            .padding(horizontal = 16.dp)
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
                    .width(94.dp)
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
                text = episode.overview,
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
                onClick = {},
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
fun EpisodeItemPreview() {
    TvManiacTheme {
        Surface {
            EpisodeItem(
                episode = episode,
            )
        }
    }
}

@ThemePreviews
@Composable
fun SeasonTitleHeaderPreview() {
    TvManiacTheme {
        Surface {
            SeasonTitleHeader(
                title = "Specials",
                episodesCount = 8,
                watchProgress = 0.5f,
                expanded = true,
            )
        }
    }
}

@ThemePreviews
@Composable
fun CollapsableContentPreview() {
    TvManiacTheme {
        Surface {
            CollapsableContent(
                episodesCount = seasonDetails.episodeCount,
                watchProgress = seasonDetails.watchProgress,
                episodeList = seasonDetails.episodes,
                headerTitle = seasonDetails.seasonName,
                collapsed = false,
            )
        }
    }
}

const val EXPANSION_TRANSITION_DURATION = 450
