package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

public data class EpisodeDetailInfo(
    val title: String,
    val imageUrl: String?,
    val episodeInfo: String,
    val overview: String? = null,
    val rating: Double? = null,
    val voteCount: Long? = null,
)

public data class SheetAction(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit,
)

@Composable
public fun EpisodeDetailBottomSheet(
    episode: EpisodeDetailInfo,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    actions: List<SheetAction> = emptyList(),
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
        ) {
            EpisodeDetailHeader(
                imageUrl = episode.imageUrl,
                contentDescription = episode.title,
            )

            EpisodeDetailContent(
                title = episode.title,
                episodeInfo = episode.episodeInfo,
                overview = episode.overview,
                rating = episode.rating,
                voteCount = episode.voteCount,
            )

            if (actions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )

                Spacer(modifier = Modifier.height(8.dp))

                actions.forEach { action ->
                    SheetActionItem(
                        icon = action.icon,
                        label = action.label,
                        onClick = action.onClick,
                    )
                }
            }
        }
    }
}

@Composable
public fun EpisodeDetailSheetContent(
    episode: EpisodeDetailInfo,
    modifier: Modifier = Modifier,
    actions: @Composable (ColumnScope.() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
    ) {
        EpisodeDetailHeader(
            imageUrl = episode.imageUrl,
            contentDescription = episode.title,
        )

        EpisodeDetailContent(
            title = episode.title,
            episodeInfo = episode.episodeInfo,
            overview = episode.overview,
            rating = episode.rating,
            voteCount = episode.voteCount,
        )

        actions?.let {
            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            Spacer(modifier = Modifier.height(8.dp))

            it()
        }
    }
}

@Composable
private fun EpisodeDetailHeader(
    imageUrl: String?,
    contentDescription: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Gray.copy(alpha = 0.8f),
                            Color.Gray,
                        ),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Movie,
                contentDescription = null,
                modifier = Modifier.size(52.dp),
                tint = Color.White.copy(alpha = 0.8f),
            )
        }

        AsyncImageComposable(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp)
                .width(32.dp)
                .height(4.dp)
                .background(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(2.dp),
                ),
        )
    }
}

@Composable
private fun EpisodeDetailContent(
    title: String,
    episodeInfo: String,
    overview: String?,
    rating: Double?,
    voteCount: Long?,
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Text(
            text = episodeInfo,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (rating != null && rating > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.secondary,
                )

                val ratingText = buildString {
                    append("%.1f".format(rating))
                    voteCount?.let { votes ->
                        append(" ($votes votes)")
                    }
                }
                Text(
                    text = ratingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        overview?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
public fun SheetActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@ThemePreviews
@Composable
private fun EpisodeDetailSheetContentPreview() {
    com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme {
        EpisodeDetailSheetContent(
            episode = EpisodeDetailInfo(
                title = "The Walking Dead: Daryl Dixon",
                imageUrl = null,
                episodeInfo = "S02E01 \u2022 45m",
                overview = "Daryl washes ashore in France and struggles to piece together how he got there and why.",
                rating = 8.5,
                voteCount = 1234,
            ),
        )
    }
}

@ThemePreviews
@Composable
private fun EpisodeDetailSheetContentWithActionsPreview() {
    com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme {
        EpisodeDetailSheetContent(
            episode = EpisodeDetailInfo(
                title = "Wednesday",
                imageUrl = null,
                episodeInfo = "S02E03 \u2022 50m",
                overview = "Wednesday arrives at Nevermore Academy and begins investigating a series of mysterious events.",
                rating = 7.9,
                voteCount = 856,
            ),
            actions = {
                SheetActionItem(
                    icon = Icons.Outlined.Movie,
                    label = "Mark as Watched",
                    onClick = {},
                )
                SheetActionItem(
                    icon = Icons.Outlined.Movie,
                    label = "Open Show",
                    onClick = {},
                )
                SheetActionItem(
                    icon = Icons.Outlined.Movie,
                    label = "Open Season",
                    onClick = {},
                )
            },
        )
    }
}

@ThemePreviews
@Composable
private fun EpisodeDetailSheetContentNoRatingPreview() {
    com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme {
        EpisodeDetailSheetContent(
            episode = EpisodeDetailInfo(
                title = "House of the Dragon",
                imageUrl = null,
                episodeInfo = "S03E01",
                overview = "King Viserys hosts a tournament to celebrate the birth of his heir.",
            ),
        )
    }
}
