package com.thomaskioko.tvmaniac.profile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.CollapsibleSection
import com.thomaskioko.tvmaniac.compose.components.InlineSectionError
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.ShimmerBox
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.ImageDimens
import com.thomaskioko.tvmaniac.compose.theme.Layout
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileShowItem
import com.thomaskioko.tvmaniac.profile.presenter.model.SectionState
import com.thomaskioko.tvmaniac.testtags.component.CollapsibleSectionTestTags
import com.thomaskioko.tvmaniac.testtags.profile.ProfileTestTags
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun FavoritesSection(
    favorites: SectionState<ProfileShowItem>,
    title: String,
    retryLabel: String,
    onShowClick: (Long) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (favorites is SectionState.Empty) return

    CollapsibleSection(
        title = title,
        modifier = modifier,
        toggleTestTag = CollapsibleSectionTestTags.toggle(ProfileTestTags.FAVORITES_SECTION_KEY),
    ) {
        val posterWidth = Layout.posterRailWidth

        when (favorites) {
            SectionState.Loading -> SkeletonRow(posterWidth = posterWidth)
            is SectionState.Error -> InlineSectionError(
                message = favorites.message.message,
                retryLabel = retryLabel,
                onRetry = onRetry,
                retryModifier = Modifier.testTag(ProfileTestTags.FAVORITES_RETRY_TEST_TAG),
            )
            is SectionState.Content -> PosterRow(
                shows = favorites.items,
                posterWidth = posterWidth,
                onShowClick = onShowClick,
            )
            SectionState.Empty -> Unit
        }
    }
}

@Composable
private fun PosterRow(
    shows: ImmutableList<ProfileShowItem>,
    posterWidth: Dp,
    onShowClick: (Long) -> Unit,
) {
    LazyRow(
        modifier = Modifier.testTag(ProfileTestTags.FAVORITES_ROW_TEST_TAG),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = shows,
            key = { it.traktId },
        ) { show ->
            PosterCard(
                imageUrl = show.posterUrl,
                title = show.title,
                imageWidth = posterWidth,
                shape = MaterialTheme.shapes.medium,
                onClick = { onShowClick(show.traktId) },
                modifier = Modifier.testTag(ProfileTestTags.showCard(show.traktId)),
            )
        }
    }
}

@Composable
private fun SkeletonRow(posterWidth: Dp) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        repeat(3) {
            ShimmerBox(
                modifier = Modifier
                    .width(posterWidth)
                    .height(posterWidth / ImageDimens.PosterAspect),
                shape = MaterialTheme.shapes.medium,
            )
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun FavoritesSectionPreview() {
    FavoritesSection(
        favorites = SectionState.Content(
            persistentListOf(
                ProfileShowItem(traktId = 1, tmdbId = 1396, title = "Breaking Bad", posterUrl = null),
                ProfileShowItem(traktId = 2, tmdbId = 1399, title = "Game of Thrones", posterUrl = null),
            ),
        ),
        title = "Favorites",
        retryLabel = "Retry",
        onShowClick = {},
        onRetry = {},
    )
}
