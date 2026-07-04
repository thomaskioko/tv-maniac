package com.thomaskioko.tvmaniac.showdetails.ui.section

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.TextLoadingItem
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_similar
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowModel
import com.thomaskioko.tvmaniac.presenter.showdetails.similar.ShowDetailsSimilarAction
import com.thomaskioko.tvmaniac.presenter.showdetails.similar.ShowDetailsSimilarPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.similar.ShowDetailsSimilarShowClicked
import com.thomaskioko.tvmaniac.presenter.showdetails.similar.ShowDetailsSimilarState
import com.thomaskioko.tvmaniac.showdetails.ui.previewSimilarState
import com.thomaskioko.tvmaniac.testtags.showdetails.ShowDetailsTestTags
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun ShowDetailsSimilarSection(presenter: ShowDetailsSimilarPresenter) {
    val state by presenter.state.collectAsState()
    ShowDetailsSimilarSection(state = state, onAction = presenter::dispatch)
}

@Composable
internal fun ShowDetailsSimilarSection(
    state: ShowDetailsSimilarState,
    onAction: (ShowDetailsSimilarAction) -> Unit,
) {
    SimilarShowsContent(
        modifier = Modifier.testTag(ShowDetailsTestTags.SIMILAR_SHOWS_LIST_TEST_TAG),
        similarShows = state.similarShows,
        onShowClicked = { showId -> onAction(ShowDetailsSimilarShowClicked(showId)) },
    )
}

@Composable
private fun SimilarShowsContent(
    similarShows: ImmutableList<ShowModel>,
    onShowClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 1.dp),
    ) {
        if (similarShows.isNotEmpty()) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))

                HorizontalRowContent(
                    title = title_similar.resolve(LocalContext.current),
                    items = similarShows,
                    onShowClicked = onShowClicked,
                )
            }
        }
    }
}

@Composable
private fun HorizontalRowContent(
    title: String,
    items: ImmutableList<ShowModel>,
    onShowClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()

    TextLoadingItem(title = title) {
        LazyRow(
            modifier = modifier,
            state = lazyListState,
            flingBehavior = rememberSnapFlingBehavior(lazyListState, SnapPosition.Start),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(
                items = items,
                key = { it.showId },
                contentType = { "ShowModel" },
            ) { tvShow ->
                PosterCard(
                    imageUrl = tvShow.posterImageUrl,
                    onClick = { onShowClicked(tvShow.showId) },
                    title = tvShow.title,
                )
            }
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ShowDetailsSimilarSectionPreview() {
    ShowDetailsSimilarSection(
        state = previewSimilarState,
        onAction = {},
    )
}
