package com.thomaskioko.tvmaniac.profile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.CollapsibleSection
import com.thomaskioko.tvmaniac.compose.components.InlineSectionError
import com.thomaskioko.tvmaniac.compose.components.ListCollageCard
import com.thomaskioko.tvmaniac.compose.components.ListsSkeletonRow
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileListItem
import com.thomaskioko.tvmaniac.profile.presenter.model.SectionState
import com.thomaskioko.tvmaniac.testtags.component.CollapsibleSectionTestTags
import com.thomaskioko.tvmaniac.testtags.profile.ProfileTestTags
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private const val MAX_INLINE_LISTS = 4
private val CollageCardWidth = 210.dp

@Composable
internal fun UserListsSection(
    userLists: SectionState<ProfileListItem>,
    title: String,
    viewAllLabel: String,
    retryLabel: String,
    onViewAll: () -> Unit,
    onListClick: (Long) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (userLists is SectionState.Empty) return

    val showViewAll = (userLists as? SectionState.Content)?.items?.size?.let { it > MAX_INLINE_LISTS } == true

    CollapsibleSection(
        title = title,
        modifier = modifier,
        showMore = showViewAll,
        moreContentDescription = viewAllLabel,
        onMoreClick = onViewAll,
        toggleTestTag = CollapsibleSectionTestTags.toggle(ProfileTestTags.USER_LISTS_SECTION_KEY),
    ) {
        when (userLists) {
            SectionState.Loading -> ListsSkeletonRow()
            is SectionState.Error -> InlineSectionError(
                message = userLists.message.message,
                retryLabel = retryLabel,
                onRetry = onRetry,
                retryModifier = Modifier.testTag(ProfileTestTags.USER_LISTS_RETRY_TEST_TAG),
            )
            is SectionState.Content -> ListsRow(
                lists = userLists.items,
                onListClick = onListClick,
            )
            SectionState.Empty -> Unit
        }
    }
}

@Composable
private fun ListsRow(
    lists: ImmutableList<ProfileListItem>,
    onListClick: (Long) -> Unit,
) {
    LazyRow(
        modifier = Modifier.testTag(ProfileTestTags.USER_LISTS_ROW_TEST_TAG),
        contentPadding = PaddingValues(horizontal = TvManiacSpacing.medium),
        horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.small),
    ) {
        items(
            items = lists,
            key = { it.id },
        ) { list ->
            ListCollageCard(
                name = list.name,
                itemCountLabel = list.itemCountLabel,
                posterUrls = list.posterUrls,
                onClick = { onListClick(list.id) },
                modifier = Modifier
                    .width(CollageCardWidth)
                    .testTag(ProfileTestTags.listCard(list.id)),
            )
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun UserListsSectionPreview() {
    UserListsSection(
        userLists = SectionState.Content(
            persistentListOf(
                ProfileListItem(
                    id = 1,
                    name = "Watchlist",
                    itemCount = 24,
                    itemCountLabel = "24 shows",
                    posterUrls = persistentListOf("/a.jpg", "/b.jpg", "/c.jpg", "/d.jpg"),
                ),
                ProfileListItem(
                    id = 2,
                    name = "Favorites",
                    itemCount = 2,
                    itemCountLabel = "2 shows",
                    posterUrls = persistentListOf("/e.jpg", "/f.jpg"),
                ),
                ProfileListItem(
                    id = 3,
                    name = "New List",
                    itemCount = 3,
                    itemCountLabel = "3 shows",
                    posterUrls = persistentListOf(),
                ),
            ),
        ),
        title = "Your Lists",
        viewAllLabel = "More",
        retryLabel = "Retry",
        onViewAll = {},
        onListClick = {},
        onRetry = {},
    )
}
