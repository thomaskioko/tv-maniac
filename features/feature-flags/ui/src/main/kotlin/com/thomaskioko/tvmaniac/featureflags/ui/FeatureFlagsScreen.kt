package com.thomaskioko.tvmaniac.featureflags.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.SearchTextContainer
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.components.TvManiacSwitch
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSortDescriptor
import com.thomaskioko.tvmaniac.featureflags.presenter.BackClicked
import com.thomaskioko.tvmaniac.featureflags.presenter.ClearAllLocals
import com.thomaskioko.tvmaniac.featureflags.presenter.ClearLocal
import com.thomaskioko.tvmaniac.featureflags.presenter.DirectionToggled
import com.thomaskioko.tvmaniac.featureflags.presenter.FeatureFlagItem
import com.thomaskioko.tvmaniac.featureflags.presenter.FeatureFlagsPresenter
import com.thomaskioko.tvmaniac.featureflags.presenter.FeatureFlagsState
import com.thomaskioko.tvmaniac.featureflags.presenter.ForceRefresh
import com.thomaskioko.tvmaniac.featureflags.presenter.GroupByTypeToggled
import com.thomaskioko.tvmaniac.featureflags.presenter.SearchQueryChanged
import com.thomaskioko.tvmaniac.featureflags.presenter.SortChanged
import com.thomaskioko.tvmaniac.featureflags.presenter.ToggleFlag
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_back
import com.thomaskioko.tvmaniac.i18n.resolve
import io.github.thomaskioko.codegen.annotations.ScreenUi

@ScreenUi(presenter = FeatureFlagsPresenter::class, parentScope = ActivityScope::class)
@Composable
public fun FeatureFlagsScreen(
    presenter: FeatureFlagsPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsState()

    FeatureFlagsScreen(
        state = state,
        onBackClicked = { presenter.dispatch(BackClicked) },
        onSearchQueryChanged = { presenter.dispatch(SearchQueryChanged(it)) },
        onResetAll = { presenter.dispatch(ClearAllLocals) },
        onForceRefresh = { presenter.dispatch(ForceRefresh) },
        onToggleFlag = { key, value -> presenter.dispatch(ToggleFlag(key, value)) },
        onResetFlag = { presenter.dispatch(ClearLocal(it)) },
        onSortChanged = { presenter.dispatch(SortChanged(it)) },
        onDirectionToggled = { presenter.dispatch(DirectionToggled) },
        onGroupByTypeToggled = { presenter.dispatch(GroupByTypeToggled) },
        modifier = modifier,
    )
}

@Composable
internal fun FeatureFlagsScreen(
    state: FeatureFlagsState,
    onBackClicked: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onResetAll: () -> Unit,
    onForceRefresh: () -> Unit,
    onToggleFlag: (String, Boolean) -> Unit,
    onResetFlag: (String) -> Unit,
    onSortChanged: (FeatureFlagSortDescriptor) -> Unit,
    onDirectionToggled: () -> Unit,
    onGroupByTypeToggled: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TvManiacTopBar(
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .clickable(onClick = onBackClicked)
                            .padding(TvManiacSpacing.medium),
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = cd_back.resolve(context),
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                },
                title = {
                    Text(
                        text = state.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = TvManiacSpacing.medium),
                    )
                },
                actions = {
                    FeatureFlagsOverflowMenu(
                        state = state,
                        onSortChanged = onSortChanged,
                        onDirectionToggled = onDirectionToggled,
                        onGroupByTypeToggled = onGroupByTypeToggled,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { innerPadding ->
        SearchTextContainer(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            query = state.searchQuery,
            hint = state.searchHint,
            lazyListState = lazyListState,
            onQueryChanged = onSearchQueryChanged,
            onClearQuery = { onSearchQueryChanged("") },
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize(),
            ) {
                item { Spacer(modifier = Modifier.height(TvManiacSpacing.xSmall)) }

                item {
                    FeatureFlagsActionRow(
                        icon = Icons.Filled.RestartAlt,
                        title = state.resetAllTitle,
                        subtitle = state.resetAllSubtitle,
                        onClick = onResetAll,
                    )
                }

                item {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        modifier = Modifier.padding(horizontal = TvManiacSpacing.large),
                    )
                }

                item {
                    FeatureFlagsActionRow(
                        icon = Icons.Filled.Refresh,
                        title = state.forceRefreshTitle,
                        subtitle = state.forceRefreshSubtitle,
                        onClick = onForceRefresh,
                    )
                }

                item {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        modifier = Modifier.padding(horizontal = TvManiacSpacing.large),
                    )
                }

                if (state.items.isEmpty()) {
                    item {
                        Text(
                            text = state.emptyResults,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(TvManiacSpacing.large),
                        )
                    }
                } else {
                    itemsIndexed(
                        items = state.items,
                        key = { _, it -> it.key },
                    ) { index, item ->
                        FeatureFlagRowItem(
                            item = item,
                            resetButtonLabel = state.resetButtonLabel,
                            onToggle = { onToggleFlag(item.key, it) },
                            onReset = { onResetFlag(item.key) },
                        )
                        if (index < state.items.lastIndex) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                modifier = Modifier.padding(horizontal = TvManiacSpacing.large),
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(TvManiacSpacing.xLarge)) }
            }
        }
    }
}

@Composable
private fun FeatureFlagsOverflowMenu(
    state: FeatureFlagsState,
    onSortChanged: (FeatureFlagSortDescriptor) -> Unit,
    onDirectionToggled: () -> Unit,
    onGroupByTypeToggled: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Sort,
                contentDescription = state.moreActionsLabel,
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            CheckableDropdownItem(
                label = state.groupByTypeLabel,
                checked = state.groupByType,
                onClick = {
                    onGroupByTypeToggled()
                    expanded = false
                },
            )

            CheckableDropdownItem(
                label = state.noGroupingLabel,
                checked = !state.groupByType,
                onClick = {
                    if (state.groupByType) onGroupByTypeToggled()
                    expanded = false
                },
            )

            HorizontalDivider()

            for (descriptor in FeatureFlagSortDescriptor.entries) {
                CheckableDropdownItem(
                    label = descriptor.label,
                    checked = state.sort == descriptor,
                    onClick = {
                        onSortChanged(descriptor)
                        expanded = false
                    },
                )
            }

            HorizontalDivider()

            CheckableDropdownItem(
                label = state.sortAscendingLabel,
                checked = state.ascending,
                onClick = {
                    if (!state.ascending) onDirectionToggled()
                    expanded = false
                },
            )
            CheckableDropdownItem(
                label = state.sortDescendingLabel,
                checked = !state.ascending,
                onClick = {
                    if (state.ascending) onDirectionToggled()
                    expanded = false
                },
            )
        }
    }
}

@Composable
private fun CheckableDropdownItem(
    label: String,
    checked: Boolean,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                )
                if (checked) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        },
        onClick = onClick,
    )
}

@Composable
private fun FeatureFlagsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = TvManiacSpacing.medium, vertical = TvManiacSpacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(TvManiacSpacing.medium))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun FeatureFlagRowItem(
    item: FeatureFlagItem,
    resetButtonLabel: String,
    onToggle: (Boolean) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = TvManiacSpacing.medium, vertical = TvManiacSpacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.width(TvManiacSpacing.xSmall))
                Text(
                    text = item.source,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = TvManiacSpacing.xxxSmall),
                )
            }
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (item.isLocal) {
            TextButton(
                onClick = onReset,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary,
                ),
            ) {
                Text(text = resetButtonLabel)
            }
        }
        TvManiacSwitch(
            checked = item.value,
            onCheckedChange = onToggle,
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun FeatureFlagsScreenPreview(
    @PreviewParameter(FeatureFlagsPreviewParameterProvider::class) state: FeatureFlagsState,
) {
    FeatureFlagsScreen(
        state = state,
        onBackClicked = {},
        onSearchQueryChanged = {},
        onResetAll = {},
        onForceRefresh = {},
        onToggleFlag = { _, _ -> },
        onResetFlag = {},
        onSortChanged = {},
        onDirectionToggled = {},
        onGroupByTypeToggled = {},
    )
}
