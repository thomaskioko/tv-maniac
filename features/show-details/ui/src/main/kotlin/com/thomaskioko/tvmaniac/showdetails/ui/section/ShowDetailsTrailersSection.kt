package com.thomaskioko.tvmaniac.showdetails.ui.section

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.TextLoadingItem
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.LocalLandscapeWidthScale
import com.thomaskioko.tvmaniac.compose.theme.TvManiacElevation
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_trailer
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.presenter.showdetails.model.TrailerModel
import com.thomaskioko.tvmaniac.presenter.showdetails.trailers.ShowDetailsTrailersAction
import com.thomaskioko.tvmaniac.presenter.showdetails.trailers.ShowDetailsTrailersPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.trailers.ShowDetailsTrailersState
import com.thomaskioko.tvmaniac.presenter.showdetails.trailers.ShowDetailsWatchTrailerClicked
import com.thomaskioko.tvmaniac.showdetails.ui.previewTrailersState
import com.thomaskioko.tvmaniac.testtags.showdetails.ShowDetailsTestTags
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun ShowDetailsTrailersSection(presenter: ShowDetailsTrailersPresenter) {
    val state by presenter.state.collectAsState()
    ShowDetailsTrailersSection(state = state, onAction = presenter::dispatch)
}

@Composable
internal fun ShowDetailsTrailersSection(
    state: ShowDetailsTrailersState,
    onAction: (ShowDetailsTrailersAction) -> Unit,
) {
    TrailersContent(
        modifier = Modifier.testTag(ShowDetailsTestTags.TRAILERS_LIST_TEST_TAG),
        trailersList = state.trailersList,
        onTrailerClicked = { id -> onAction(ShowDetailsWatchTrailerClicked(id)) },
    )
}

@Composable
private fun TrailersContent(
    trailersList: ImmutableList<TrailerModel>,
    onTrailerClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (trailersList.isEmpty()) return

    Spacer(modifier = Modifier.height(TvManiacSpacing.medium))

    TextLoadingItem(
        title = title_trailer.resolve(LocalContext.current),
        modifier = modifier,
    ) {
        val lazyListState = rememberLazyListState()
        val scrimColor = TvManiacTheme.colorScheme.scrim

        LazyRow(
            state = lazyListState,
            flingBehavior = rememberSnapFlingBehavior(lazyListState, SnapPosition.Start),
            contentPadding = PaddingValues(horizontal = TvManiacSpacing.medium),
            horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.xSmall),
        ) {
            items(
                items = trailersList,
                key = { it.key },
                contentType = { "Trailer" },
            ) { trailer ->
                Column {
                    Card(
                        onClick = { onTrailerClicked(trailer.showId) },
                        shape = MaterialTheme.shapes.small,
                        elevation = CardDefaults.cardElevation(defaultElevation = TvManiacElevation.medium),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                    ) {
                        Box {
                            AsyncImageComposable(
                                model = trailer.youtubeThumbnailUrl,
                                contentDescription = trailer.name,
                                modifier = Modifier
                                    .height(140.dp * LocalLandscapeWidthScale.current)
                                    .aspectRatio(3 / 1.5f)
                                    .drawWithCache {
                                        val gradient = Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, scrimColor),
                                            startY = size.height / 3,
                                            endY = size.height,
                                        )
                                        onDrawWithContent {
                                            drawContent()
                                            drawRect(gradient, blendMode = BlendMode.Multiply)
                                        }
                                    },
                            )

                            Icon(
                                imageVector = Icons.Filled.PlayCircle,
                                contentDescription = trailer.name,
                                tint = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(48.dp),
                            )
                        }
                    }

                    Text(
                        text = trailer.name,
                        modifier = Modifier
                            .padding(vertical = TvManiacSpacing.xSmall)
                            .widthIn(0.dp, 280.dp),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Normal,
                        ),
                    )
                }
            }
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ShowDetailsTrailersSectionPreview() {
    ShowDetailsTrailersSection(
        state = previewTrailersState,
        onAction = {},
    )
}
