package com.thomaskioko.showdetails

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.components.TvShowCard
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.showcommon.api.model.TvShow
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun SimilarShowsContent(
    similarShows: List<TvShow>,
    onShowClicked: (Long) -> Unit = {}
) {
    val lazyListState = rememberLazyListState()

    if (similarShows.isNotEmpty()) {
        ColumnSpacer(8)

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.title_similar),
                style = MaterialTheme.typography.h6,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            ColumnSpacer(4)

            LazyRow(
                state = lazyListState,
                flingBehavior = rememberSnapperFlingBehavior(lazyListState),
            ) {
                itemsIndexed(similarShows) { index, tvShow ->
                    TvShowCard(
                        posterImageUrl = tvShow.posterImageUrl,
                        title = tvShow.title,
                        isFirstCard = index == 0,
                        onClick = { onShowClicked(tvShow.id) },
                        imageWidth = 84.dp,
                        rowSpacer = 0
                    )
                }
            }
        }
    }
}

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SimilarShowsShowsContentPreview() {
    TvManiacTheme {
        Surface {
            SimilarShowsContent(
                similarShows = showList,
            )
        }
    }
}
