package com.thomaskioko.tvmaniac.settings.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.thomaskioko.tvmaniac.compose.components.PremiumOverlay
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.settings.presenter.SettingsLocks
import com.thomaskioko.tvmaniac.settings.presenter.ThemeModel
import com.thomaskioko.tvmaniac.testtags.settings.SettingsTestTags

private const val THEME_GRID_COLUMNS = 3

@Composable
internal fun ThemeSelectorSection(
    selectedTheme: ThemeModel,
    onThemeSelected: (ThemeModel) -> Unit,
    onUpgradeClick: () -> Unit,
    locks: SettingsLocks,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val themes = ThemeModel.sortedByDisplayOrder
    val freeThemes = themes.filterNot { it.isPremium }
    val premiumThemes = themes.filter { it.isPremium }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = TvManiacSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.xSmall),
    ) {
        ThemeGrid(
            themes = freeThemes,
            selectedTheme = selectedTheme,
            onThemeSelected = onThemeSelected,
        )

        PremiumOverlay(
            locked = locks.customThemesLocked,
            badgeText = locks.badgeText,
            title = locks.themesLockedTitle,
            message = locks.themesLockedMessage,
            actionText = locks.upgradeText,
            onActionClick = onUpgradeClick,
            modifier = Modifier.testTag(SettingsTestTags.THEMES_LOCKED),
        ) {
            ThemeGrid(
                themes = premiumThemes,
                selectedTheme = selectedTheme,
                onThemeSelected = onThemeSelected,
            )
        }
    }
}

@Composable
private fun ThemeGrid(
    themes: List<ThemeModel>,
    selectedTheme: ThemeModel,
    onThemeSelected: (ThemeModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.xSmall),
    ) {
        themes.chunked(THEME_GRID_COLUMNS).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.xSmall),
            ) {
                row.forEach { theme ->
                    ThemePreviewSwatch(
                        theme = theme,
                        displayName = theme.getDisplayName(context),
                        isSelected = theme == selectedTheme,
                        onClick = { onThemeSelected(theme) },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(THEME_GRID_COLUMNS - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ThemeModel.getDisplayName(context: Context): String {
    return displayNameKey.resourceId.resolve(context)
}
