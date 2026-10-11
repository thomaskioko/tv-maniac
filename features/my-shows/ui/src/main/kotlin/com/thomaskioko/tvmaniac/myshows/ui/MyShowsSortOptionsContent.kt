package com.thomaskioko.tvmaniac.myshows.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.thomaskioko.tvmaniac.compose.components.FilterChipSection
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.i18n.MR
import com.thomaskioko.tvmaniac.i18n.label_library_sort_added_asc
import com.thomaskioko.tvmaniac.i18n.label_library_sort_added_desc
import com.thomaskioko.tvmaniac.i18n.label_library_sort_by
import com.thomaskioko.tvmaniac.i18n.label_library_sort_released_asc
import com.thomaskioko.tvmaniac.i18n.label_library_sort_released_desc
import com.thomaskioko.tvmaniac.i18n.label_library_sort_title_asc
import com.thomaskioko.tvmaniac.i18n.label_library_sort_title_desc
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.watchlistprefs.api.model.WatchlistSortOption
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun MyShowsSortOptionsContent(
    selectedSortOption: WatchlistSortOption,
    onSortOptionSelected: (WatchlistSortOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = TvManiacSpacing.medium)
            .navigationBarsPadding()
            .verticalScroll(scrollState),
    ) {
        FilterChipSection(
            title = MR.strings.label_library_sort_by.resolve(context),
            items = WatchlistSortOption.entries.toImmutableList(),
            selectedItems = persistentSetOf(selectedSortOption),
            onItemToggle = { onSortOptionSelected(it) },
            labelProvider = { sortOption ->
                when (sortOption) {
                    WatchlistSortOption.ADDED_DESC -> MR.strings.label_library_sort_added_desc.resolve(context)
                    WatchlistSortOption.ADDED_ASC -> MR.strings.label_library_sort_added_asc.resolve(context)
                    WatchlistSortOption.RELEASED_DESC -> MR.strings.label_library_sort_released_desc.resolve(context)
                    WatchlistSortOption.RELEASED_ASC -> MR.strings.label_library_sort_released_asc.resolve(context)
                    WatchlistSortOption.TITLE_ASC -> MR.strings.label_library_sort_title_asc.resolve(context)
                    WatchlistSortOption.TITLE_DESC -> MR.strings.label_library_sort_title_desc.resolve(context)
                }
            },
            collapsedItemCount = 6,
            singleSelect = true,
        )

        Spacer(modifier = Modifier.height(TvManiacSpacing.large))
    }
}
