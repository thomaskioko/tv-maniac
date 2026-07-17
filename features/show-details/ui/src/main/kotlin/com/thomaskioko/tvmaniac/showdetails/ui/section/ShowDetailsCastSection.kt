package com.thomaskioko.tvmaniac.showdetails.ui.section

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.thomaskioko.tvmaniac.compose.components.CastCard
import com.thomaskioko.tvmaniac.compose.components.TextLoadingItem
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_casts
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.presenter.showdetails.cast.ShowDetailsCastPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.cast.ShowDetailsCastState
import com.thomaskioko.tvmaniac.presenter.showdetails.model.CastModel
import com.thomaskioko.tvmaniac.showdetails.ui.previewCastState
import com.thomaskioko.tvmaniac.testtags.showdetails.ShowDetailsTestTags
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun ShowDetailsCastSection(presenter: ShowDetailsCastPresenter) {
    val state by presenter.state.collectAsState()
    ShowDetailsCastSection(state = state)
}

@Composable
internal fun ShowDetailsCastSection(state: ShowDetailsCastState) {
    CastContent(
        modifier = Modifier.testTag(ShowDetailsTestTags.CAST_LIST_TEST_TAG),
        castsList = state.castsList,
    )
}

@Composable
private fun CastContent(
    castsList: ImmutableList<CastModel>,
    modifier: Modifier = Modifier,
) {
    if (castsList.isEmpty()) return

    TextLoadingItem(
        title = title_casts.resolve(LocalContext.current),
        modifier = modifier,
    ) {
        Box(contentAlignment = Alignment.BottomCenter) {
            val lazyListState = rememberLazyListState()

            LazyRow(
                modifier = Modifier,
                state = lazyListState,
                flingBehavior = rememberSnapFlingBehavior(lazyListState, SnapPosition.Start),
                contentPadding = PaddingValues(horizontal = TvManiacSpacing.medium),
                horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.xSmall),
            ) {
                items(
                    items = castsList,
                    key = { it.id },
                    contentType = { "Cast" },
                ) { cast ->
                    CastCard(
                        profileUrl = cast.profileUrl,
                        name = cast.name,
                        characterName = cast.characterName,
                    )
                }
            }
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ShowDetailsCastSectionPreview() {
    ShowDetailsCastSection(state = previewCastState)
}
