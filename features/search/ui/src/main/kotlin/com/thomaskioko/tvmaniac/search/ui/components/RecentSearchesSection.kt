package com.thomaskioko.tvmaniac.search.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.thomaskioko.tvmaniac.compose.components.BoxTextItems
import com.thomaskioko.tvmaniac.compose.components.SelectableFilterChip
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.i18n.MR.strings.btn_search_clear_recent
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_search_recent
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.testtags.search.SearchTestTags
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun RecentSearchesSection(
    recentSearches: ImmutableList<String>,
    onRecentSearchClicked: (String) -> Unit,
    onClearRecentSearches: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = TvManiacSpacing.xSmall)
            .testTag(SearchTestTags.RECENT_SEARCHES_SECTION_TEST_TAG),
        verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.xSmall),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = TvManiacSpacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BoxTextItems(title = label_search_recent.resolve(context))

            Text(
                text = btn_search_clear_recent.resolve(context),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .clickable(role = Role.Button, onClick = onClearRecentSearches)
                    .testTag(SearchTestTags.CLEAR_RECENT_SEARCHES_TEST_TAG),
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = TvManiacSpacing.medium),
            horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.xSmall),
        ) {
            items(
                items = recentSearches,
                key = { it },
                contentType = { "RecentSearchChip" },
            ) { query ->
                SelectableFilterChip(
                    label = query,
                    isSelected = false,
                    onClick = { onRecentSearchClicked(query) },
                    modifier = Modifier.clearAndSetSemantics {
                        testTag = SearchTestTags.recentSearchChip(query)
                        role = Role.Button
                        contentDescription = query
                        onClick {
                            onRecentSearchClicked(query)
                            true
                        }
                    },
                )
            }
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun RecentSearchesSectionPreview() {
    RecentSearchesSection(
        recentSearches = persistentListOf("Breaking Bad", "Loki", "The Bear"),
        onRecentSearchClicked = {},
        onClearRecentSearches = {},
    )
}
