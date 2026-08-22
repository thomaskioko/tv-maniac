package com.thomaskioko.tvmaniac.settings.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.thomaskioko.tvmaniac.compose.components.SelectableFilterChip
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.domain.theme.ImageQuality
import com.thomaskioko.tvmaniac.settings.presenter.ImageQualitySelected
import com.thomaskioko.tvmaniac.settings.presenter.SettingsActions
import com.thomaskioko.tvmaniac.settings.presenter.SettingsLabels
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.settings.presenter.ThemeSelected
import com.thomaskioko.tvmaniac.settings.presenter.UpgradeToPremiumClicked
import com.thomaskioko.tvmaniac.settings.ui.SettingsGroup
import com.thomaskioko.tvmaniac.settings.ui.SettingsSectionLabel
import com.thomaskioko.tvmaniac.settings.ui.ThemeSelectorSection
import com.thomaskioko.tvmaniac.settings.ui.appearanceLockedState
import com.thomaskioko.tvmaniac.settings.ui.appearanceState
import com.thomaskioko.tvmaniac.testtags.settings.SettingsTestTags

@Composable
internal fun AppearancePage(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.testTag(SettingsTestTags.LIST_TEST_TAG)) {
        item { Spacer(modifier = Modifier.height(TvManiacSpacing.medium)) }

        item { SettingsSectionLabel(text = state.labels.themeTitle) }

        item {
            SettingsGroup {
                ThemeSelectorSection(
                    selectedTheme = state.theme,
                    onThemeSelected = { onAction(ThemeSelected(it)) },
                    onUpgradeClick = { onAction(UpgradeToPremiumClicked) },
                    premium = state.premium,
                    modifier = Modifier.padding(vertical = TvManiacSpacing.small),
                )
            }
        }

        item { Spacer(modifier = Modifier.height(TvManiacSpacing.large)) }

        item { SettingsSectionLabel(text = state.labels.imageQualityTitle) }

        item {
            SettingsGroup {
                Column(modifier = Modifier.padding(horizontal = TvManiacSpacing.medium, vertical = TvManiacSpacing.medium)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.xSmall)) {
                        ImageQuality.entries.forEach { quality ->
                            SelectableFilterChip(
                                modifier = Modifier.testTag(SettingsTestTags.imageQualityChip(quality.name)),
                                label = state.labels.imageQualityLabel(quality),
                                isSelected = state.imageQuality == quality,
                                onClick = { onAction(ImageQualitySelected(quality)) },
                            )
                        }
                    }
                    Text(
                        text = state.labels.imageQualityDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = TvManiacSpacing.xSmall),
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(TvManiacSpacing.large)) }
    }
}

private fun SettingsLabels.imageQualityLabel(quality: ImageQuality): String = when (quality) {
    ImageQuality.AUTO -> imageQualityAuto
    ImageQuality.HIGH -> imageQualityHigh
    ImageQuality.MEDIUM -> imageQualityMedium
    ImageQuality.LOW -> imageQualityLow
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

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun AppearancePageLockedPreview() {
    AppearancePage(
        state = appearanceLockedState,
        onAction = {},
    )
}
