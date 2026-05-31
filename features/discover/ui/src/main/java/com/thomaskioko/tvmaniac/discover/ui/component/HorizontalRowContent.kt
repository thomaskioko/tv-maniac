package com.thomaskioko.tvmaniac.discover.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.BoxTextItems
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.discover.presenter.model.DiscoverShow
import com.thomaskioko.tvmaniac.discover.ui.discoverContentSuccess
import com.thomaskioko.tvmaniac.i18n.MR.strings.str_more
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.testtags.discover.DiscoverTestTags
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun HorizontalRowContent(
    modifier: Modifier = Modifier,
    category: String,
    rowKey: String,
    tvShows: ImmutableList<DiscoverShow>,
    onItemClicked: (Long) -> Unit,
    onMoreClicked: () -> Unit,
) {
    Box(modifier = modifier) {
        AnimatedVisibility(visible = tvShows.isNotEmpty()) {
            Column {
                BoxTextItems(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                    title = category,
                    label = str_more.resolve(LocalContext.current),
                    onMoreClicked = onMoreClicked,
                    moreModifier = Modifier.testTag(DiscoverTestTags.moreButton(rowKey)),
                )

                val lazyListState = rememberLazyListState()

                LazyRow(
                    state = lazyListState,
                    flingBehavior = rememberSnapperFlingBehavior(lazyListState),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(
                        items = tvShows,
                        key = { tvShow -> "${rowKey}_${tvShow.traktId}" },
                        contentType = { "ShowModel" },
                    ) { tvShow ->
                        PosterCard(
                            imageUrl = tvShow.posterImageUrl,
                            onClick = { onItemClicked(tvShow.traktId) },
                            modifier = Modifier.testTag(DiscoverTestTags.showCard(rowKey, tvShow.traktId)),
                            title = tvShow.title,
                            isInLibrary = tvShow.inLibrary,
                        )
                    }
                }
            }
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
internal fun HorizontalRowContentPreview() {
    HorizontalRowContent(
        modifier = Modifier.height(220.dp),
        category = "Trending",
        rowKey = DiscoverTestTags.ROW_KEY_TRENDING,
        tvShows = discoverContentSuccess.topRatedShows,
        onItemClicked = {},
        onMoreClicked = {},
    )
}
