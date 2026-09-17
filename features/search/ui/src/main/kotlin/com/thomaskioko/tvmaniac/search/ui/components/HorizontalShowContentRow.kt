package com.thomaskioko.tvmaniac.search.ui.components

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.thomaskioko.tvmaniac.compose.components.BoxTextItems
import com.thomaskioko.tvmaniac.compose.components.PosterBackdropCard
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.i18n.MR.strings.str_more
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.search.presenter.model.ShowItem
import com.thomaskioko.tvmaniac.testtags.search.SearchTestTags
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun HorizontalShowContentRow(
    title: String,
    tvShows: ImmutableList<ShowItem>?,
    slug: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    onItemClicked: (Long) -> Unit,
    onMoreClicked: () -> Unit,
) {
    val lazyListState = rememberLazyListState()

    if (tvShows.isNullOrEmpty()) return
    Column(modifier = modifier) {
        BoxTextItems(
            title = title,
            subtitle = description,
            label = str_more.resolve(LocalContext.current),
            onMoreClicked = onMoreClicked,
            moreModifier = Modifier.testTag(SearchTestTags.genreMoreButton(slug)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = TvManiacSpacing.medium, vertical = TvManiacSpacing.xSmall),
        )

        LazyRow(
            state = lazyListState,
            flingBehavior = rememberSnapFlingBehavior(lazyListState, SnapPosition.Start),
            contentPadding = PaddingValues(start = TvManiacSpacing.medium),
            horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.xSmall),
        ) {
            items(
                items = tvShows,
                key = { it.showId },
                contentType = { "HorizontalShowItem" },
            ) { tvShow ->
                PosterBackdropCard(
                    imageUrl = tvShow.posterImageUrl,
                    title = tvShow.title,
                    onClick = { onItemClicked(tvShow.showId) },
                )
            }
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun HorizontalRowContentPreview() {
    HorizontalShowContentRow(
        tvShows = List(5) {
            ShowItem(
                showId = 84958,
                tmdbId = 84958,
                title = "Loki",
                posterImageUrl = null,
                overview = "After stealing the Tesseract during the events of Avengers: Endgame.",
                status = "Ended",
                inLibrary = false,
            )
        }.toImmutableList(),
        onItemClicked = {},
        onMoreClicked = {},
        title = "Being watched",
        description = "Non-stop thrill and action",
        slug = "action",
    )
}
