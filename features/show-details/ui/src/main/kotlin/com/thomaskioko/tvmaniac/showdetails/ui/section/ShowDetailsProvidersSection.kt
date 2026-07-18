package com.thomaskioko.tvmaniac.showdetails.ui.section

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.TextLoadingItem
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacElevation
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_providers
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_providers_label
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ProviderModel
import com.thomaskioko.tvmaniac.presenter.showdetails.providers.ShowDetailsProvidersPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.providers.ShowDetailsProvidersState
import com.thomaskioko.tvmaniac.showdetails.ui.previewProvidersState
import com.thomaskioko.tvmaniac.testtags.showdetails.ShowDetailsTestTags
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun ShowDetailsProvidersSection(presenter: ShowDetailsProvidersPresenter) {
    val state by presenter.state.collectAsState()
    ShowDetailsProvidersSection(state = state)
}

@Composable
internal fun ShowDetailsProvidersSection(state: ShowDetailsProvidersState) {
    WatchProvider(
        modifier = Modifier.testTag(ShowDetailsTestTags.WATCH_PROVIDERS_SECTION_TEST_TAG),
        list = state.providers,
    )
}

@Composable
private fun WatchProvider(
    list: ImmutableList<ProviderModel>,
    modifier: Modifier = Modifier,
) {
    if (list.isEmpty()) return

    Spacer(modifier = Modifier.height(TvManiacSpacing.xSmall))

    val context = LocalContext.current

    TextLoadingItem(
        title = title_providers.resolve(context),
        subTitle = title_providers_label.resolve(context),
    ) {
        val lazyListState = rememberLazyListState()

        LazyRow(
            modifier = modifier,
            state = lazyListState,
            flingBehavior = rememberSnapFlingBehavior(lazyListState, SnapPosition.Start),
            contentPadding = PaddingValues(horizontal = TvManiacSpacing.medium),
            horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.xxSmall),
        ) {
            items(
                items = list,
                key = { it.name },
                contentType = { "WatchProvider" },
            ) { tvShow ->
                Card(
                    modifier = Modifier.size(width = 80.dp, height = 60.dp),
                    shape = MaterialTheme.shapes.small,
                    elevation = CardDefaults.cardElevation(defaultElevation = TvManiacElevation.large),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                ) {
                    AsyncImageComposable(
                        model = tvShow.logoUrl,
                        contentDescription = tvShow.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .animateItem(),
                    )
                }
            }
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ShowDetailsProvidersSectionPreview() {
    ShowDetailsProvidersSection(state = previewProvidersState)
}
