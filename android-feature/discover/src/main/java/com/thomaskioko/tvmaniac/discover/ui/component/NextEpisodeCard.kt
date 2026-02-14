package com.thomaskioko.tvmaniac.discover.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.discover.presenter.model.NextEpisodeUiModel
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_mark_watched
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_open_season
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_unfollow_show
import com.thomaskioko.tvmaniac.i18n.resolve

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun NextEpisodeCard(
    episode: NextEpisodeUiModel,
    onEpisodeClick: (Long, Long) -> Unit,
    onMarkWatched: () -> Unit,
    onUnfollowShow: () -> Unit,
    onOpenSeason: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = modifier
            .width(300.dp)
            .height(160.dp)
            .combinedClickable(
                onClick = { onEpisodeClick(episode.showTraktId, episode.episodeId) },
                onLongClick = { showMenu = true },
            ),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImageComposable(
                model = episode.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop,
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f),
                            ),
                            startY = 0f,
                            endY = 400f,
                        ),
                    ),
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = episode.showName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = episode.episodeNumberFormatted,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 1,
                )
            }

            episode.runtime?.let { runtime ->
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            shape = MaterialTheme.shapes.small,
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = runtime,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                    )
                }
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
            ) {
                DropdownMenuItem(
                    text = { Text(menu_mark_watched.resolve(context)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                        )
                    },
                    onClick = {
                        showMenu = false
                        onMarkWatched()
                    },
                )
                DropdownMenuItem(
                    text = { Text(menu_unfollow_show.resolve(context)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.RemoveCircleOutline,
                            contentDescription = null,
                        )
                    },
                    onClick = {
                        showMenu = false
                        onUnfollowShow()
                    },
                )
                DropdownMenuItem(
                    text = { Text(menu_open_season.resolve(context)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Folder,
                            contentDescription = null,
                        )
                    },
                    onClick = {
                        showMenu = false
                        onOpenSeason()
                    },
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun NextEpisodeCardPreview() {
    TvManiacTheme {
        NextEpisodeCard(
            episode = NextEpisodeUiModel(
                showTraktId = 1L,
                showName = "The Walking Dead: Daryl Dixon",
                imageUrl = "/still.jpg",
                episodeId = 123L,
                episodeTitle = "L'âme Perdue",
                episodeNumberFormatted = "S02E01",
                seasonId = 1L,
                seasonNumber = 2,
                episodeNumber = 1,
                runtime = "45 min",
                overview = "Daryl washes ashore in France and struggles to piece together how he got there and why.",
                isNew = true,
            ),
            onEpisodeClick = { _, _ -> },
            onMarkWatched = {},
            onUnfollowShow = {},
            onOpenSeason = {},
        )
    }
}
