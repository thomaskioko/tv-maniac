package com.thomaskioko.tvmaniac.discover.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.BoxTextItems
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.discover.presenter.model.DiscoverShow
import com.thomaskioko.tvmaniac.discover.ui.discoverContentSuccess
import com.thomaskioko.tvmaniac.i18n.MR.strings.str_more
import com.thomaskioko.tvmaniac.i18n.resolve
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun HorizontalRowContent(
    modifier: Modifier = Modifier,
    category: String,
    tvShows: ImmutableList<DiscoverShow>,
    onItemClicked: (Long) -> Unit,
    onMoreClicked: () -> Unit,
) {
    AnimatedVisibility(visible = tvShows.isNotEmpty()) {
        Column(modifier = modifier) {
            BoxTextItems(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                title = category,
                label = str_more.resolve(LocalContext.current),
                onMoreClicked = onMoreClicked,
            )

            val lazyListState = rememberLazyListState()

            LazyRow(
                state = lazyListState,
                flingBehavior = rememberSnapperFlingBehavior(lazyListState),
            ) {
                itemsIndexed(
                    items = tvShows,
                    key = { index, tvShow -> "${category}_${tvShow.tmdbId}_$index" },
                ) { index, tvShow ->
                    val value = if (index == 0) 16 else 8

                    Spacer(modifier = Modifier.width(value.dp))

                    PosterCard(
                        imageUrl = tvShow.posterImageUrl,
                        title = tvShow.title,
                        onClick = { onItemClicked(tvShow.tmdbId) },
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
internal fun HorizontalRowContentPreview() {
    TvManiacTheme {
        HorizontalRowContent(
            modifier = Modifier.height(240.dp),
            category = "Trending",
            tvShows = discoverContentSuccess.topRatedShows,
            onItemClicked = {},
            onMoreClicked = {},
        )
    }
}
