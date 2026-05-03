package com.thomaskioko.tvmaniac.episodedetail.ui

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.testtags.episodesheet.EpisodeSheetTestTags

internal data class EpisodeDetailInfo(
    val title: String,
    val imageUrl: String?,
    val episodeInfo: String,
    val overview: String? = null,
    val rating: Double? = null,
    val voteCount: Long? = null,
)

internal data class SheetAction(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit,
)

@Composable
internal fun EpisodeDetailBottomSheet(
    episode: EpisodeDetailInfo,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable (ColumnScope.() -> Unit)? = null,
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
                .testTag(EpisodeSheetTestTags.SHEET_TEST_TAG)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
        ) {
            EpisodeDetailHeader(
                imageUrl = episode.imageUrl,
                contentDescription = episode.title,
            )

            EpisodeDetailContentLayout(
                title = episode.title,
                episodeInfo = episode.episodeInfo,
                overview = episode.overview,
                rating = episode.rating,
                voteCount = episode.voteCount,
            )

            actions?.let { actions ->
                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )

                Spacer(modifier = Modifier.height(8.dp))

                actions()
            }
        }
    }
}

@Composable
internal fun EpisodeDetailSheetContent(
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

        EpisodeDetailContentLayout(
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
        val brush = remember {
            Brush.verticalGradient(
                colors = listOf(
                    Color.Gray.copy(alpha = 0.8f),
                    Color.Gray,
                ),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush),
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
private fun EpisodeDetailContentLayout(
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
            modifier = Modifier
                .testTag(EpisodeSheetTestTags.TITLE_TEST_TAG),
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
internal fun SheetActionItem(
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
