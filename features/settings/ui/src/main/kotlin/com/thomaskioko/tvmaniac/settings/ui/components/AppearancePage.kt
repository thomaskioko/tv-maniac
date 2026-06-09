package com.thomaskioko.tvmaniac.settings.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.domain.theme.ImageQuality
import com.thomaskioko.tvmaniac.settings.presenter.ImageQualitySelected
import com.thomaskioko.tvmaniac.settings.presenter.SettingsActions
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.settings.presenter.ThemeSelected
import com.thomaskioko.tvmaniac.settings.ui.SettingsGroup
import com.thomaskioko.tvmaniac.settings.ui.SettingsSectionLabel
import com.thomaskioko.tvmaniac.settings.ui.ThemeSelectorSection
import com.thomaskioko.tvmaniac.settings.ui.appearanceState
import com.thomaskioko.tvmaniac.testtags.settings.SettingsTestTags

@Composable
internal fun AppearancePage(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.testTag(SettingsTestTags.LIST_TEST_TAG)) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item { SettingsSectionLabel(text = state.labels.themeTitle) }

        item {
            SettingsGroup {
                ThemeSelectorSection(
                    selectedTheme = state.theme,
                    onThemeSelected = { onAction(ThemeSelected(it)) },
                    modifier = Modifier.padding(vertical = 12.dp),
                )
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        item { SettingsSectionLabel(text = state.labels.imageQualityTitle) }

        item {
            SettingsGroup {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ImageQualityChip(
                            label = state.labels.imageQualityAuto,
                            quality = ImageQuality.AUTO,
                            isSelected = state.imageQuality == ImageQuality.AUTO,
                            onClick = { onAction(ImageQualitySelected(ImageQuality.AUTO)) },
                        )
                        ImageQualityChip(
                            label = state.labels.imageQualityHigh,
                            quality = ImageQuality.HIGH,
                            isSelected = state.imageQuality == ImageQuality.HIGH,
                            onClick = { onAction(ImageQualitySelected(ImageQuality.HIGH)) },
                        )
                        ImageQualityChip(
                            label = state.labels.imageQualityMedium,
                            quality = ImageQuality.MEDIUM,
                            isSelected = state.imageQuality == ImageQuality.MEDIUM,
                            onClick = { onAction(ImageQualitySelected(ImageQuality.MEDIUM)) },
                        )
                        ImageQualityChip(
                            label = state.labels.imageQualityLow,
                            quality = ImageQuality.LOW,
                            isSelected = state.imageQuality == ImageQuality.LOW,
                            onClick = { onAction(ImageQualitySelected(ImageQuality.LOW)) },
                        )
                    }
                    Text(
                        text = state.labels.imageQualityDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun ImageQualityChip(
    label: String,
    quality: ImageQuality,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
        modifier = Modifier.testTag(SettingsTestTags.imageQualityChip(quality.name)),
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                color = if (isSelected) {
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
            enabled = true,
            selected = isSelected,
        ),
        shape = RoundedCornerShape(20.dp),
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun AppearancePagePreview() {
    AppearancePage(
        state = appearanceState,
        onAction = {},
    )
}
