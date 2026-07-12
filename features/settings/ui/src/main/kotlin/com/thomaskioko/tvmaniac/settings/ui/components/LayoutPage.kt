package com.thomaskioko.tvmaniac.settings.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.SwitchRow
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.settings.presenter.BlurUnwatchedToggled
import com.thomaskioko.tvmaniac.settings.presenter.FontSizeChanged
import com.thomaskioko.tvmaniac.settings.presenter.HapticFeedbackToggled
import com.thomaskioko.tvmaniac.settings.presenter.OpenSettingsPage
import com.thomaskioko.tvmaniac.settings.presenter.SeasonOrderToggled
import com.thomaskioko.tvmaniac.settings.presenter.SettingsActions
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPage
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.settings.ui.SettingsGroup
import com.thomaskioko.tvmaniac.settings.ui.SettingsGroupDivider
import com.thomaskioko.tvmaniac.settings.ui.SettingsNavigationRow
import com.thomaskioko.tvmaniac.settings.ui.fontSizeScaledLayoutState
import com.thomaskioko.tvmaniac.settings.ui.layoutState
import com.thomaskioko.tvmaniac.testtags.settings.SettingsTestTags
import kotlin.math.roundToInt

@Composable
internal fun LayoutPage(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.testTag(SettingsTestTags.LIST_TEST_TAG)) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            SettingsGroup {
                SwitchRow(
                    modifier = Modifier.testTag(SettingsTestTags.HAPTIC_FEEDBACK_TOGGLE_TEST_TAG),
                    icon = Icons.Filled.Vibration,
                    title = state.labels.hapticFeedbackTitle,
                    description = state.labels.hapticFeedbackDescription,
                    checked = state.hapticFeedbackEnabled,
                    onCheckedChange = { onAction(HapticFeedbackToggled(it)) },
                )

                SwitchRow(
                    modifier = Modifier.testTag(SettingsTestTags.SEASON_ORDER_TOGGLE_TEST_TAG),
                    icon = Icons.Filled.SwapVert,
                    title = state.labels.seasonOrderTitle,
                    description = state.labels.seasonOrderDescription,
                    checked = state.newestSeasonFirst,
                    onCheckedChange = { onAction(SeasonOrderToggled(it)) },
                )

                SwitchRow(
                    modifier = Modifier.testTag(SettingsTestTags.BLUR_UNWATCHED_TOGGLE_TEST_TAG),
                    icon = Icons.Filled.VisibilityOff,
                    title = state.labels.blurUnwatchedTitle,
                    description = state.labels.blurUnwatchedDescription,
                    checked = state.blurImage,
                    onCheckedChange = { onAction(BlurUnwatchedToggled(it)) },
                )

                SettingsGroupDivider()

                SettingsNavigationRow(
                    modifier = Modifier.testTag(SettingsTestTags.DISCOVER_SECTIONS_ROW_TEST_TAG),
                    icon = Icons.Filled.GridView,
                    title = state.labels.discoverSectionsTitle,
                    description = state.labels.discoverSectionsDescription,
                    onClick = { onAction(OpenSettingsPage(SettingsPage.DISCOVER_SECTIONS)) },
                )
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        item {
            SettingsGroup {
                FontSizeSection(
                    title = state.labels.fontSizeTitle,
                    description = state.labels.fontSizeDescription,
                    previewText = state.labels.fontSizePreview,
                    resetLabel = state.labels.fontSizeReset,
                    fontSizePercent = state.fontSizePercent,
                    onFontSizeChanged = { onAction(FontSizeChanged(it)) },
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun FontSizeSection(
    title: String,
    description: String,
    previewText: String,
    resetLabel: String,
    fontSizePercent: Int,
    onFontSizeChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var sliderPosition by remember(fontSizePercent) { mutableFloatStateOf(fontSizePercent.toFloat()) }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (sliderPosition.roundToInt() != DEFAULT_FONT_SIZE_PERCENT) {
                TextButton(
                    modifier = Modifier.testTag(SettingsTestTags.FONT_SIZE_RESET_BUTTON_TEST_TAG),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary,
                    ),
                    onClick = {
                        sliderPosition = DEFAULT_FONT_SIZE_PERCENT.toFloat()
                        onFontSizeChanged(DEFAULT_FONT_SIZE_PERCENT)
                    },
                ) {
                    Text(
                        text = resetLabel,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }

            Text(
                text = "${sliderPosition.roundToInt()}%",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.medium,
        ) {
            Text(
                text = previewText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(12.dp),
            )
        }

        Slider(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(SettingsTestTags.FONT_SIZE_SLIDER_TEST_TAG),
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            onValueChangeFinished = { onFontSizeChanged(sliderPosition.roundToInt()) },
            valueRange = FONT_SIZE_MIN_PERCENT.toFloat()..FONT_SIZE_MAX_PERCENT.toFloat(),
            steps = FONT_SIZE_SLIDER_STEPS,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                activeTickColor = MaterialTheme.colorScheme.onSecondary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                inactiveTickColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
    }
}

private const val FONT_SIZE_MIN_PERCENT = 85
private const val FONT_SIZE_MAX_PERCENT = 130
private const val FONT_SIZE_SLIDER_STEPS = 8
private const val DEFAULT_FONT_SIZE_PERCENT = 100

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun LayoutPagePreview() {
    LayoutPage(
        state = layoutState,
        onAction = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun LayoutPageFontSizeScaledPreview() {
    LayoutPage(
        state = fontSizeScaledLayoutState,
        onAction = {},
    )
}
