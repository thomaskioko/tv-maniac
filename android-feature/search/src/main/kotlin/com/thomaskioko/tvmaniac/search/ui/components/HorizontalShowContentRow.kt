package com.thomaskioko.tvmaniac.search.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.BoxTextItems
import com.thomaskioko.tvmaniac.compose.components.PosterBackdropCard
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.search.presenter.model.ShowItem
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun HorizontalShowContentRow(
    title: String,
    tvShows: ImmutableList<ShowItem>?,
    modifier: Modifier = Modifier,
    description: String? = null,
    onItemClicked: (Long) -> Unit,
) {
    val lazyListState = rememberLazyListState()

    if (tvShows.isNullOrEmpty()) return
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BoxTextItems(
                title = title,
                subtitle = description,
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
            )
        }

        LazyRow(
            state = lazyListState,
            flingBehavior = rememberSnapperFlingBehavior(lazyListState),
            contentPadding = PaddingValues(start = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(tvShows) { tvShow ->
                PosterBackdropCard(
                    imageUrl = tvShow.posterImageUrl,
                    title = tvShow.title,
                    imageWidth = 240.dp,
                    aspectRatio = 4 / 3f,
                    onClick = { onItemClicked(tvShow.traktId) },
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun HorizontalRowContentPreview() {
    TvManiacTheme {
        Surface {
            HorizontalShowContentRow(
                tvShows = List(5) {
                    ShowItem(
                        traktId = 84958,
                        tmdbId = 84958,
                        title = "Loki",
                        posterImageUrl = null,
                        overview = "After stealing the Tesseract during the events of Avengers: Endgame.",
                        status = "Ended",
                        inLibrary = false,
                    )
                }.toImmutableList(),
                onItemClicked = {},
                title = "Being watched",
                description = "Non-stop thrill and action",
            )
        }
    }
}
