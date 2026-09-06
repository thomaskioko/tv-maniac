package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.TvManiacElevation
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private val CardWidth = 210.dp

public object ListCollageCardDefaults {
    public val Height: Dp = 140.dp
}

@Composable
public fun ListCollageCard(
    name: String,
    itemCountLabel: String,
    posterUrls: ImmutableList<String>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = TvManiacElevation.medium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Box {
            PosterCollage(
                posterUrls = posterUrls,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ListCollageCardDefaults.Height),
            )

            val scrimColor = TvManiacTheme.colorScheme.scrim
            val scrim = remember(scrimColor) {
                Brush.verticalGradient(
                    listOf(
                        Color.Transparent,
                        scrimColor.copy(alpha = 0.85f),
                    ),
                )
            }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(scrim),
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = TvManiacSpacing.small, vertical = TvManiacSpacing.small),
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = itemCountLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun PosterCollage(
    posterUrls: ImmutableList<String>,
    modifier: Modifier = Modifier,
) {
    when {
        posterUrls.isEmpty() -> CollagePlaceholder(modifier = modifier)
        posterUrls.size == 1 -> CollageCell(
            posterUrl = posterUrls[0],
            modifier = modifier,
        )
        else -> Column(modifier = modifier) {
            Row(modifier = Modifier.weight(1f)) {
                CollageCell(posterUrl = posterUrls.getOrNull(0), modifier = Modifier.weight(1f).fillMaxSize())
                CollageCell(posterUrl = posterUrls.getOrNull(1), modifier = Modifier.weight(1f).fillMaxSize())
            }
            Row(modifier = Modifier.weight(1f)) {
                CollageCell(posterUrl = posterUrls.getOrNull(2), modifier = Modifier.weight(1f).fillMaxSize())
                CollageCell(posterUrl = posterUrls.getOrNull(3), modifier = Modifier.weight(1f).fillMaxSize())
            }
        }
    }
}

@Composable
private fun CollageCell(
    posterUrl: String?,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
        if (posterUrl != null) {
            AsyncImageComposable(
                model = posterUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun CollagePlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.List,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(40.dp),
        )
    }
}

@Composable
public fun ListsSkeletonRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(horizontal = TvManiacSpacing.medium),
        horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.small),
    ) {
        repeat(3) {
            ShimmerBox(
                modifier = Modifier
                    .width(CardWidth)
                    .height(ListCollageCardDefaults.Height),
                shape = MaterialTheme.shapes.large,
            )
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ListCollageCardPreview() {
    ListCollageCard(
        name = "Watchlist",
        itemCountLabel = "24 shows",
        posterUrls = persistentListOf("/a.jpg", "/b.jpg", "/c.jpg", "/d.jpg"),
        onClick = {},
        modifier = Modifier.width(CardWidth),
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ListCollageCardEmptyPreview() {
    ListCollageCard(
        name = "New List",
        itemCountLabel = "0 shows",
        posterUrls = persistentListOf(),
        onClick = {},
        modifier = Modifier.width(CardWidth),
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ListsSkeletonRowPreview() {
    ListsSkeletonRow()
}
