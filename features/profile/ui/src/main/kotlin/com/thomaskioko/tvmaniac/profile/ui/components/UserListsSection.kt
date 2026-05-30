package com.thomaskioko.tvmaniac.profile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.BoxTextItems
import com.thomaskioko.tvmaniac.compose.components.InlineSectionError
import com.thomaskioko.tvmaniac.compose.components.ShimmerBox
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileListItem
import com.thomaskioko.tvmaniac.profile.presenter.model.SectionState
import com.thomaskioko.tvmaniac.testtags.profile.ProfileTestTags
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private val CardWidth = 210.dp
private val CollageHeight = 140.dp
private const val MAX_INLINE_LISTS = 4

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
    when (userLists) {
        SectionState.Loading -> UserListsSkeleton(title = title, modifier = modifier)
        SectionState.Empty -> Unit
        is SectionState.Error -> UserListsError(
            title = title,
            message = userLists.message.message,
            retryLabel = retryLabel,
            onRetry = onRetry,
            modifier = modifier,
        )
        is SectionState.Content -> UserListsRow(
            title = title,
            viewAllLabel = viewAllLabel,
            lists = userLists.items,
            onViewAll = onViewAll,
            onListClick = onListClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun UserListsRow(
    title: String,
    viewAllLabel: String,
    lists: ImmutableList<ProfileListItem>,
    onViewAll: () -> Unit,
    onListClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(
            title = title,
            viewAllLabel = viewAllLabel.takeIf { lists.size > MAX_INLINE_LISTS },
            onViewAll = onViewAll,
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.testTag(ProfileTestTags.USER_LISTS_ROW_TEST_TAG),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                items = lists,
                key = { it.id },
            ) { list ->
                ListCollageCard(
                    list = list,
                    onClick = { onListClick(list.id) },
                )
            }
        }
    }
}

@Composable
private fun ListCollageCard(
    list: ProfileListItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .width(CardWidth)
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .testTag(ProfileTestTags.listCard(list.id)),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Box {
            PosterCollage(
                posterUrls = list.posterUrls,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CollageHeight),
            )

            val scrim = remember {
                Brush.verticalGradient(
                    listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.85f),
                    ),
                )
            }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(scrim),
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Text(
                    text = list.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = list.itemCountLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun PosterCollage(
    posterUrls: ImmutableList<String>,
    modifier: Modifier = Modifier,
) {
    when {
        posterUrls.isEmpty() -> CollagePlaceholder(modifier = modifier)
        posterUrls.size == 1 -> CollageCell(
            posterUrl = posterUrls[0],
            modifier = modifier,
        )
        else -> Column(modifier = modifier) {
            Row(modifier = Modifier.weight(1f)) {
                CollageCell(posterUrl = posterUrls.getOrNull(0), modifier = Modifier.weight(1f).fillMaxSize())
                CollageCell(posterUrl = posterUrls.getOrNull(1), modifier = Modifier.weight(1f).fillMaxSize())
            }
            Row(modifier = Modifier.weight(1f)) {
                CollageCell(posterUrl = posterUrls.getOrNull(2), modifier = Modifier.weight(1f).fillMaxSize())
                CollageCell(posterUrl = posterUrls.getOrNull(3), modifier = Modifier.weight(1f).fillMaxSize())
            }
        }
    }
}

@Composable
private fun CollageCell(
    posterUrl: String?,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
        if (posterUrl != null) {
            AsyncImageComposable(
                model = posterUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun CollagePlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.List,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(40.dp),
        )
    }
}

@Composable
private fun UserListsSkeleton(
    title: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(title = title, viewAllLabel = null, onViewAll = {})

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            repeat(3) {
                ShimmerBox(
                    modifier = Modifier
                        .width(CardWidth)
                        .height(CollageHeight),
                    shape = MaterialTheme.shapes.large,
                )
            }
        }
    }
}

@Composable
private fun UserListsError(
    title: String,
    message: String?,
    retryLabel: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(title = title, viewAllLabel = null, onViewAll = {})

        InlineSectionError(
            message = message ?: "",
            retryLabel = retryLabel,
            onRetry = onRetry,
            retryModifier = Modifier.testTag(ProfileTestTags.USER_LISTS_RETRY_TEST_TAG),
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    viewAllLabel: String?,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxTextItems(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp),
        title = title,
        label = viewAllLabel,
        onMoreClicked = onViewAll,
        moreModifier = Modifier.testTag(ProfileTestTags.USER_LISTS_VIEW_ALL_TEST_TAG),
    )
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
