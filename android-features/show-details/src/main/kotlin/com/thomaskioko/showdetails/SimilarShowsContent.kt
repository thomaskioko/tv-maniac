package com.thomaskioko.showdetails

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.TextLoadingItem
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvPosterCard
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.presentation.showdetails.model.Show
import com.thomaskioko.tvmaniac.resources.R
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun SimilarShowsContent(
    isLoading: Boolean,
    similarShows: List<Show>,
    modifier: Modifier = Modifier,
    onShowClicked: (Long) -> Unit = {},
) {
    val lazyListState = rememberLazyListState()

    TextLoadingItem(
        isLoading = isLoading,
        text = stringResource(id = R.string.title_similar),
    )

    LazyRow(
        modifier = modifier,
        state = lazyListState,
        flingBehavior = rememberSnapperFlingBehavior(lazyListState),
    ) {
        itemsIndexed(similarShows) { index, tvShow ->
            val value = if (index == 0) 16 else 4

            Spacer(modifier = Modifier.width(value.dp))

            TvPosterCard(
                posterImageUrl = tvShow.posterImageUrl,
                title = tvShow.title,
                onClick = { onShowClicked(tvShow.traktId) },
                imageWidth = 84.dp,
            )
        }
    }
}

@ThemePreviews
@Composable
fun SimilarShowsShowsContentPreview() {
    TvManiacTheme {
        Surface {
            SimilarShowsContent(
                isLoading = false,
                similarShows = showList,
            )
        }
    }
}
