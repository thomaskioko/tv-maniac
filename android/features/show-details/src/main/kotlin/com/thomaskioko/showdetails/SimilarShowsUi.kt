package com.thomaskioko.showdetails

import android.content.res.Configuration
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.LoadingRowContent
import com.thomaskioko.tvmaniac.compose.components.TvShowCard
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.data.showdetails.model.Show
import com.thomaskioko.tvmaniac.resources.R
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun SimilarShowsUi(
    isLoading: Boolean,
    similarShows: List<Show>,
    onShowClicked: (Long) -> Unit = {}
) {
    val lazyListState = rememberLazyListState()

    LoadingRowContent(
        isLoading = isLoading,
        text = stringResource(id = R.string.title_similar),
        content = {

            LazyRow(
                state = lazyListState,
                flingBehavior = rememberSnapperFlingBehavior(lazyListState),
            ) {
                itemsIndexed(similarShows) { index, tvShow ->
                    TvShowCard(
                        posterImageUrl = tvShow.posterImageUrl,
                        title = tvShow.title,
                        isFirstCard = index == 0,
                        onClick = { onShowClicked(tvShow.traktId) },
                        imageWidth = 84.dp,
                        rowSpacer = 0
                    )
                }
            }
        }
    )

}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SimilarShowsShowsContentPreview() {
    TvManiacTheme {
        Surface {
            SimilarShowsUi(
                isLoading = false,
                similarShows = showList,
            )
        }
    }
}
