package com.thomaskioko.tvmaniac.search.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.thomaskioko.tvmaniac.compose.components.ShimmerBox
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.ImageType
import com.thomaskioko.tvmaniac.compose.theme.Layout
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing

private const val SHIMMER_ITEM_COUNT = 9

@Composable
internal fun SearchResultsShimmer(
    modifier: Modifier = Modifier,
    itemCount: Int = SHIMMER_ITEM_COUNT,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(Layout.posterColumns),
        userScrollEnabled = false,
        contentPadding = PaddingValues(horizontal = TvManiacSpacing.medium),
        horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.xSmall),
        verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.xSmall),
        modifier = modifier.fillMaxSize(),
    ) {
        items(itemCount) {
            Column(verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.xxSmall)) {
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(ImageType.Poster.aspect),
                )

                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(TvManiacSpacing.small),
                )
            }
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun SearchResultsShimmerPreview() {
    SearchResultsShimmer()
}
