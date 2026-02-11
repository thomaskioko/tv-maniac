package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_library_filter_show_less
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_library_filter_show_more
import com.thomaskioko.tvmaniac.i18n.resolve

@OptIn(ExperimentalLayoutApi::class)
@Composable
public fun <T> FilterChipSection(
    title: String,
    items: List<T>,
    selectedItems: Set<T>,
    onItemToggle: (T) -> Unit,
    labelProvider: (T) -> String,
    modifier: Modifier = Modifier,
    collapsedItemCount: Int = 5,
    singleSelect: Boolean = false,
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }
    val visibleItems = if (isExpanded) items else items.take(collapsedItemCount)
    val hasMoreItems = items.size > collapsedItemCount

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
    ) {
        SectionHeader(title = title)

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            visibleItems.forEach { item ->
                val isSelected = item in selectedItems
                SelectableFilterChip(
                    label = labelProvider(item),
                    isSelected = isSelected,
                    onClick = { onItemToggle(item) },
                )
            }
        }

        if (hasMoreItems) {
            Spacer(modifier = Modifier.height(8.dp))
            ShowMoreToggle(
                isExpanded = isExpanded,
                showMoreText = label_library_filter_show_more.resolve(context),
                showLessText = label_library_filter_show_less.resolve(context),
                onToggle = { isExpanded = !isExpanded },
            )
        }
    }
}

@Composable
public fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }
}

@Composable
public fun SelectableFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        modifier = modifier,
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        shape = RoundedCornerShape(8.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            selectedContainerColor = MaterialTheme.colorScheme.secondary,
            labelColor = MaterialTheme.colorScheme.onSurface,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            selectedBorderColor = Color.Transparent,
            enabled = true,
            selected = isSelected,
        ),
    )
}

@Composable
internal fun ShowMoreToggle(
    isExpanded: Boolean,
    showMoreText: String,
    showLessText: String,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable { onToggle() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = if (isExpanded) showLessText else showMoreText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Icon(
            imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview
@Composable
private fun FilterChipSectionPreview() {
    TvManiacTheme {
        Surface {
            FilterChipSection(
                title = "GENRES",
                items = listOf(
                    "Action & Adventure",
                    "Animation",
                    "Comedy",
                    "Crime",
                    "Drama",
                    "Fantasy",
                    "Sci-Fi",
                ),
                selectedItems = setOf("Drama", "Comedy"),
                onItemToggle = {},
                labelProvider = { it },
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Preview
@Composable
private fun FilterChipSectionCollapsedPreview() {
    TvManiacTheme {
        Surface {
            FilterChipSection(
                title = "STATUS",
                items = listOf(
                    "Returning Series",
                    "Planned",
                    "In Production",
                    "Ended",
                    "Canceled",
                ),
                selectedItems = setOf("Returning Series"),
                onItemToggle = {},
                labelProvider = { it },
                collapsedItemCount = 3,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Preview
@Composable
private fun SectionHeaderPreview() {
    TvManiacTheme {
        Surface {
            SectionHeader(
                title = "SORT BY",
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@ThemePreviews
@Composable
private fun SelectableFilterChipPreview() {
    TvManiacTheme {
        Surface {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp),
            ) {
                SelectableFilterChip(
                    label = "Last watched ↓",
                    isSelected = true,
                    onClick = {},
                )
                SelectableFilterChip(
                    label = "Alphabetical",
                    isSelected = false,
                    onClick = {},
                )
            }
        }
    }
}
