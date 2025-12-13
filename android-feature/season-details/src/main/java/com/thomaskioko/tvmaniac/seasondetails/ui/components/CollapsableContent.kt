package com.thomaskioko.tvmaniac.seasondetails.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.thomaskioko.tvmaniac.compose.components.ShowLinearProgressIndicator
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_navigate_back
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_episodes
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.seasondetails.presenter.EpisodeClicked
import com.thomaskioko.tvmaniac.seasondetails.presenter.OnEpisodeHeaderClicked
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsAction
import com.thomaskioko.tvmaniac.seasondetails.presenter.ToggleSeasonWatched
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.EpisodeDetailsModel
import com.thomaskioko.tvmaniac.seasondetails.ui.seasonDetailsLoaded
import kotlinx.collections.immutable.ImmutableList

@Composable
fun CollapsableContent(
    episodesCount: Long,
    watchProgress: Float,
    episodeDetailsModelList: ImmutableList<EpisodeDetailsModel>,
    collapsed: Boolean,
    isSeasonWatched: Boolean,
    onAction: (SeasonDetailsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        SeasonTitleHeader(
            episodesCount = episodesCount,
            watchProgress = watchProgress,
            isSeasonWatched = isSeasonWatched,
            expanded = !collapsed,
            onAction = onAction,
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (!collapsed) {
            episodeDetailsModelList.forEach { episode ->
                Spacer(modifier = Modifier.height(8.dp))

                EpisodeItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 84.dp),
                    imageUrl = episode.imageUrl,
                    title = episode.episodeNumberTitle,
                    episodeOverview = episode.overview,
                    onEpisodeClicked = { EpisodeClicked(episode.id) },
                    onAction = {},
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
private fun SeasonTitleHeader(
    episodesCount: Long,
    watchProgress: Float,
    expanded: Boolean,
    isSeasonWatched: Boolean,
    onAction: (SeasonDetailsAction) -> Unit,
    shape: Shape = MaterialTheme.shapes.small,
) {
    val transitionState = remember {
        MutableTransitionState(expanded).apply { targetState = !expanded }
    }

    val transition = rememberTransition(transitionState)
    val arrowRotationDegree by
        transition.animateFloat(
            label = "rotationDegreeTransition",
            transitionSpec = { tween(durationMillis = EXPANSION_TRANSITION_DURATION) },
            targetValueByState = { if (expanded) 0f else 180f },
        )

    Card(
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable { onAction(OnEpisodeHeaderClicked) },
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize(),
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
                text = title_episodes.resolve(LocalContext.current),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.constrainAs(episodeTitle) {
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
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.constrainAs(count) {
                    end.linkTo(watchedStatusIcon.start, 8.dp)
                    top.linkTo(watchedStatusIcon.top)
                    bottom.linkTo(watchedStatusIcon.bottom)

                    width = Dimension.preferredWrapContent
                },
            )

            IconButton(
                onClick = { onAction(ToggleSeasonWatched) },
                modifier = Modifier.constrainAs(watchedStatusIcon) {
                    centerVerticallyTo(parent)
                    end.linkTo(parent.end, 8.dp)
                },
            ) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    imageVector = if (isSeasonWatched) Icons.Rounded.CheckCircle else Icons.Outlined.CheckCircle,
                    contentDescription = cd_navigate_back.resolve(LocalContext.current),
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }

            ShowLinearProgressIndicator(
                progress = watchProgress,
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

@ThemePreviews
@Composable
private fun SeasonTitleHeaderPreview() {
    TvManiacTheme {
        Surface {
            SeasonTitleHeader(
                episodesCount = 8,
                watchProgress = 0.5f,
                expanded = true,
                isSeasonWatched = true,
                onAction = {},
            )
        }
    }
}

@ThemePreviews
@Composable
private fun CollapsableContentPreview() {
    TvManiacTheme {
        Surface {
            CollapsableContent(
                episodesCount = seasonDetailsLoaded.episodeCount,
                watchProgress = seasonDetailsLoaded.watchProgress,
                episodeDetailsModelList = seasonDetailsLoaded.episodeDetailsList,
                collapsed = false,
                isSeasonWatched = false,
                onAction = {},
            )
        }
    }
}

const val EXPANSION_TRANSITION_DURATION = 450
