package com.thomaskioko.tvmaniac.search.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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

    tvShows?.takeIf { it.isNotEmpty() } ?: return
    Column(modifier = modifier) {
        BoxTextItems(
            modifier = Modifier.padding(vertical = 8.dp),
            title = "$title >",
            subtitle = description,
        )

        LazyRow(
            state = lazyListState,
            flingBehavior = rememberSnapperFlingBehavior(lazyListState),
        ) {
            itemsIndexed(tvShows) { index, tvShow ->

                val value = if (index == 0) 0 else 8
                Spacer(modifier = Modifier.width(value.dp))

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
