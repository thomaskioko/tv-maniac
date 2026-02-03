package com.thomaskioko.tvmaniac.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_library_filter_apply
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_library_filter_clear
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_library_filter_genres
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_library_filter_status
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_library_sort_alphabetical
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_library_sort_by
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_library_sort_episodes_left_asc
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_library_sort_episodes_left_desc
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_library_sort_last_watched_asc
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_library_sort_last_watched_desc
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_library_sort_new_episodes
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_library_status_canceled
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_library_status_ended
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_library_status_in_production
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_library_status_planned
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_library_status_returning
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.presentation.library.LibraryState
import com.thomaskioko.tvmaniac.presentation.library.model.LibrarySortOption
import com.thomaskioko.tvmaniac.presentation.library.model.ShowStatus
import com.thomaskioko.tvmaniac.ui.library.preview.LibraryStatePreviewParameterProvider

@Composable
internal fun SortOptionsContent(
    state: LibraryState,
    onSortOptionSelected: (LibrarySortOption) -> Unit,
    onGenreToggle: (String) -> Unit,
    onStatusToggle: (ShowStatus) -> Unit,
    onClearFilters: () -> Unit,
    onApplyFilters: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .navigationBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(scrollState),
        ) {
            FilterChipSection(
                title = label_library_sort_by.resolve(context),
                items = LibrarySortOption.entries,
                selectedItems = setOf(state.sortOption),
                onItemToggle = { onSortOptionSelected(it) },
                labelProvider = { sortOption ->
                    when (sortOption) {
                        LibrarySortOption.LAST_WATCHED_DESC -> label_library_sort_last_watched_desc.resolve(context)
                        LibrarySortOption.LAST_WATCHED_ASC -> label_library_sort_last_watched_asc.resolve(context)
                        LibrarySortOption.NEW_EPISODES -> label_library_sort_new_episodes.resolve(context)
                        LibrarySortOption.EPISODES_LEFT_DESC -> label_library_sort_episodes_left_desc.resolve(context)
                        LibrarySortOption.EPISODES_LEFT_ASC -> label_library_sort_episodes_left_asc.resolve(context)
                        LibrarySortOption.ALPHABETICAL -> label_library_sort_alphabetical.resolve(context)
                    }
                },
                collapsedItemCount = 5,
                singleSelect = true,
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (state.availableGenres.isNotEmpty()) {
                FilterChipSection(
                    title = label_library_filter_genres.resolve(context),
                    items = state.availableGenres,
                    selectedItems = state.selectedGenres,
                    onItemToggle = { onGenreToggle(it) },
                    labelProvider = { it },
                    collapsedItemCount = 5,
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            if (state.availableStatuses.isNotEmpty()) {
                FilterChipSection(
                    title = label_library_filter_status.resolve(context),
                    items = state.availableStatuses,
                    selectedItems = state.selectedStatuses,
                    onItemToggle = { onStatusToggle(it) },
                    labelProvider = { status ->
                        when (status) {
                            ShowStatus.RETURNING_SERIES -> label_library_status_returning.resolve(context)
                            ShowStatus.PLANNED -> label_library_status_planned.resolve(context)
                            ShowStatus.IN_PRODUCTION -> label_library_status_in_production.resolve(context)
                            ShowStatus.ENDED -> label_library_status_ended.resolve(context)
                            ShowStatus.CANCELED -> label_library_status_canceled.resolve(context)
                        }
                    },
                    collapsedItemCount = 5,
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        FilterActionBar(
            clearText = label_library_filter_clear.resolve(context),
            applyText = label_library_filter_apply.resolve(context),
            onClearClick = onClearFilters,
            onApplyClick = onApplyFilters,
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun FilterActionBar(
    clearText: String,
    applyText: String,
    onClearClick: () -> Unit,
    onApplyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedButton(
            onClick = onClearClick,
            shape = RoundedCornerShape(24.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.DeleteOutline,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = clearText,
                modifier = Modifier.padding(start = 4.dp),
            )
        }

        Button(
            onClick = onApplyClick,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.onSecondary,
                containerColor = MaterialTheme.colorScheme.secondary,
            ),
        ) {
            Text(text = applyText)
        }
    }
}

@Preview
@Composable
private fun SortOptionsContentPreview(
    @PreviewParameter(LibraryStatePreviewParameterProvider::class) state: LibraryState,
) {
    TvManiacTheme {
        SortOptionsContent(
            state = state,
            onSortOptionSelected = {},
            onGenreToggle = {},
            onStatusToggle = {},
            onClearFilters = {},
            onApplyFilters = {},
        )
    }
}
