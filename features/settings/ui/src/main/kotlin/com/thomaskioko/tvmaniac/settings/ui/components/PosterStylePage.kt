package com.thomaskioko.tvmaniac.settings.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.CustomThemes
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.ImageDimens
import com.thomaskioko.tvmaniac.datastore.api.PosterCornerStyle
import com.thomaskioko.tvmaniac.datastore.api.PosterWidth
import com.thomaskioko.tvmaniac.settings.presenter.LandscapeWidthSelected
import com.thomaskioko.tvmaniac.settings.presenter.PosterCornerStyleSelected
import com.thomaskioko.tvmaniac.settings.presenter.PosterStyleLabels
import com.thomaskioko.tvmaniac.settings.presenter.PosterStyleReset
import com.thomaskioko.tvmaniac.settings.presenter.PosterWidthSelected
import com.thomaskioko.tvmaniac.settings.presenter.SettingsActions
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.settings.presenter.UpgradeToPremiumClicked
import com.thomaskioko.tvmaniac.settings.ui.posterStyleLockedState
import com.thomaskioko.tvmaniac.settings.ui.posterStyleMixedState
import com.thomaskioko.tvmaniac.settings.ui.posterStyleState
import com.thomaskioko.tvmaniac.testtags.settings.SettingsTestTags

private val POSTER_PREVIEW_BASE_WIDTH = 96.dp
private val LANDSCAPE_PREVIEW_BASE_WIDTH = 152.dp

@Composable
internal fun PosterStylePage(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    val locked = state.locks.posterStyleLocked
    val labels = state.labels.posterStyle

    LazyColumn(modifier = modifier.testTag(SettingsTestTags.LIST_TEST_TAG)) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            CustomThemes(
                locked = locked,
                badgeText = state.locks.badgeText,
                title = state.locks.themesLockedTitle,
                message = state.locks.themesLockedMessage,
                actionText = state.locks.upgradeText,
                onActionClick = { onAction(UpgradeToPremiumClicked) },
                modifier = Modifier.testTag(SettingsTestTags.POSTER_STYLE_LOCKED_TEST_TAG),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        PosterStyleHeader(
                            title = labels.title,
                            resetLabel = labels.reset,
                            enabled = !locked,
                            onReset = { onAction(PosterStyleReset) },
                        )

                        Text(
                            text = labels.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    PosterStyleLivePreview(
                        title = labels.livePreview,
                        postersLabel = labels.postersLabel,
                        landscapeLabel = labels.landscapeLabel,
                        posterWidth = state.posterWidth,
                        landscapeWidth = state.landscapeWidth,
                        cornerStyle = state.posterCornerStyle,
                    )

                    PosterStyleControl(
                        title = labels.postersLabel,
                        options = PosterWidth.entries,
                        selected = state.posterWidth,
                        enabled = !locked,
                        label = labels::widthLabel,
                        testTagFor = { SettingsTestTags.posterWidthChip(it.name) },
                        onSelected = { onAction(PosterWidthSelected(it)) },
                    )

                    PosterStyleControl(
                        title = labels.landscapeLabel,
                        options = PosterWidth.entries,
                        selected = state.landscapeWidth,
                        enabled = !locked,
                        label = labels::widthLabel,
                        testTagFor = { SettingsTestTags.landscapeWidthChip(it.name) },
                        onSelected = { onAction(LandscapeWidthSelected(it)) },
                    )

                    PosterStyleControl(
                        title = labels.cornerLabel,
                        options = PosterCornerStyle.entries,
                        selected = state.posterCornerStyle,
                        enabled = !locked,
                        label = labels::cornerStyleLabel,
                        testTagFor = { SettingsTestTags.posterCornerStyleChip(it.name) },
                        onSelected = { onAction(PosterCornerStyleSelected(it)) },
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun PosterStyleHeader(
    title: String,
    resetLabel: String,
    enabled: Boolean,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        TextButton(
            modifier = Modifier.testTag(SettingsTestTags.POSTER_STYLE_RESET_BUTTON_TEST_TAG),
            enabled = enabled,
            onClick = onReset,
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.secondary,
            ),
        ) {
            Text(
                text = resetLabel,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun PosterStyleLivePreview(
    title: String,
    postersLabel: String,
    landscapeLabel: String,
    posterWidth: PosterWidth,
    landscapeWidth: PosterWidth,
    cornerStyle: PosterCornerStyle,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(SettingsTestTags.POSTER_STYLE_PREVIEW_TEST_TAG),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            PosterStylePreviewCard(
                label = postersLabel,
                baseWidth = POSTER_PREVIEW_BASE_WIDTH,
                width = posterWidth,
                aspectRatio = ImageDimens.PosterAspect,
                cornerStyle = cornerStyle,
                modifier = Modifier.testTag(SettingsTestTags.POSTER_STYLE_PREVIEW_ROW_TEST_TAG),
            )
            PosterStylePreviewCard(
                label = landscapeLabel,
                baseWidth = LANDSCAPE_PREVIEW_BASE_WIDTH,
                width = landscapeWidth,
                aspectRatio = ImageDimens.BackdropAspect,
                cornerStyle = cornerStyle,
                modifier = Modifier.testTag(SettingsTestTags.POSTER_STYLE_PREVIEW_GRID_TEST_TAG),
            )
        }
    }
}

@Composable
private fun PosterStylePreviewCard(
    label: String,
    baseWidth: Dp,
    width: PosterWidth,
    aspectRatio: Float,
    cornerStyle: PosterCornerStyle,
    modifier: Modifier = Modifier,
) {
    val previewWidth by animateDpAsState(
        targetValue = baseWidth * width.scale,
        label = "poster_style_preview_width",
    )
    val previewCorner by animateDpAsState(
        targetValue = cornerStyle.cornerRadius.dp,
        label = "poster_style_preview_corner",
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .width(previewWidth)
                .aspectRatio(aspectRatio)
                .clip(RoundedCornerShape(previewCorner))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(previewCorner),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Movie,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp),
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T> PosterStyleControl(
    title: String,
    options: List<T>,
    selected: T,
    enabled: Boolean,
    label: (T) -> String,
    testTagFor: (T) -> String,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            options.forEach { option ->
                FilterChip(
                    modifier = Modifier.testTag(testTagFor(option)),
                    selected = option == selected,
                    enabled = enabled,
                    onClick = { onSelected(option) },
                    label = {
                        Text(
                            text = label(option),
                            color = if (option == selected) {
                                MaterialTheme.colorScheme.onSecondary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondary,
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = MaterialTheme.colorScheme.outline,
                        selectedBorderColor = MaterialTheme.colorScheme.secondary,
                        enabled = enabled,
                        selected = option == selected,
                    ),
                )
            }
        }
    }
}

private fun PosterStyleLabels.widthLabel(width: PosterWidth): String = when (width) {
    PosterWidth.COMPACT -> widthCompact
    PosterWidth.STANDARD -> widthStandard
    PosterWidth.COMFORTABLE -> widthComfortable
    PosterWidth.LARGE -> widthLarge
}

private fun PosterStyleLabels.cornerStyleLabel(style: PosterCornerStyle): String = when (style) {
    PosterCornerStyle.SHARP -> cornerSharp
    PosterCornerStyle.CLASSIC -> cornerClassic
    PosterCornerStyle.ROUNDED -> cornerRounded
    PosterCornerStyle.PILL -> cornerPill
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun PosterStylePagePreview() {
    PosterStylePage(
        state = posterStyleState,
        onAction = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun PosterStylePageLockedPreview() {
    PosterStylePage(
        state = posterStyleLockedState,
        onAction = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun PosterStylePageMixedPreview() {
    PosterStylePage(
        state = posterStyleMixedState,
        onAction = {},
    )
}
