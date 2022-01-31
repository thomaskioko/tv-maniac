package com.thomaskioko.tvmaniac.seasons

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.theme.green
import com.thomaskioko.tvmaniac.compose.theme.yellow300
import com.thomaskioko.tvmaniac.seasonepisodes.api.model.SeasonWithEpisodes

@Composable
fun SeasonEpisodeList(
    season: SeasonWithEpisodes,
    onEpisodeClicked: (Long) -> Unit = {},
    index: Int,
    collapsedState: SnapshotStateList<Boolean>
) {

    val isCollapsed = collapsedState[index]

    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp)
            .clickable {
                collapsedState[index] = !isCollapsed
            },
    ) {

        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {

            val (episodeTitle, image, episodeCount, watchedStatusIcon, watchlistProgress) = createRefs()

            Icon(
                Icons.Default.run {
                    if (isCollapsed) Icons.Rounded.ExpandMore else Icons.Rounded.ExpandLess
                },
                contentDescription = null,
                tint = LightGray,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .constrainAs(image) {
                        start.linkTo(parent.start, 8.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(watchlistProgress.top)

                        height = Dimension.fillToConstraints
                    },
            )

            Text(
                text = season.seasonName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.h6,
                modifier = Modifier
                    .constrainAs(episodeTitle) {
                        start.linkTo(image.end, 8.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(watchlistProgress.top)

                        width = Dimension.preferredWrapContent
                    }
            )

            Text(
                text = "${season.episodeCount}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .constrainAs(episodeCount) {
                        end.linkTo(watchedStatusIcon.start, 8.dp)
                        top.linkTo(watchedStatusIcon.top)
                        bottom.linkTo(watchedStatusIcon.bottom)

                        width = Dimension.preferredWrapContent
                    }
            )

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
                    tint = if (season.watchProgress == 1f) green else LightGray,
                    modifier = Modifier
                        .size(28.dp)
                )
            }

            LinearProgressIndicator(
                progress = season.watchProgress,
                color = if (season.watchProgress == 1f) green else yellow300,
                backgroundColor = LightGray,
                modifier = Modifier
                    .constrainAs(watchlistProgress) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    }
            )
        }
    }

    ColumnSpacer(value = 8)

    if (!isCollapsed) {
        season.episodes.forEach { episode ->
            EpisodeListItem(
                episode,
                onEpisodeClicked = onEpisodeClicked
            )
        }
    }
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SeasonHeaderPreview() {
    TvManiacTheme {
        Surface {
            SeasonEpisodeList(
                season = seasonsEpList.first(),
                collapsedState = SnapshotStateList(),
                index = 0
            )
        }
    }
}
