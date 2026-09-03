package com.thomaskioko.tvmaniac.settings.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.ChoiceChipGroup
import com.thomaskioko.tvmaniac.compose.components.PremiumOverlay
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.compose.theme.toColorScheme
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.settings.presenter.SettingsActions
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.settings.presenter.ThemeModel
import com.thomaskioko.tvmaniac.settings.presenter.UpgradeToPremiumClicked
import com.thomaskioko.tvmaniac.settings.presenter.WidgetThemeSelected
import com.thomaskioko.tvmaniac.settings.ui.WidgetAppearancePreviewParameterProvider
import com.thomaskioko.tvmaniac.testtags.settings.SettingsTestTags

private const val MATCH_APP_THEME_TAG = "match_app"
private val PREVIEW_POSTER_WIDTH = 40.dp
private val PREVIEW_POSTER_HEIGHT = 60.dp
private val PREVIEW_WIDGET_HEIGHT = 176.dp

@Composable
internal fun WidgetAppearancePage(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    val locked = state.premium.widgetThemingLocked
    val labels = state.labels.widgetAppearance
    val themes = listOf<ThemeModel?>(null) + ThemeModel.sortedByDisplayOrder

    PremiumOverlay(
        locked = locked,
        badgeText = state.premium.badgeText,
        title = state.premium.themesLockedTitle,
        message = state.premium.themesLockedMessage,
        actionText = state.premium.upgradeText,
        onActionClick = { onAction(UpgradeToPremiumClicked) },
        modifier = modifier
            .fillMaxSize()
            .testTag(SettingsTestTags.WIDGET_APPEARANCE_LOCKED_TEST_TAG),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .testTag(SettingsTestTags.LIST_TEST_TAG)
                .padding(horizontal = TvManiacSpacing.medium)
                .padding(top = TvManiacSpacing.medium, bottom = TvManiacSpacing.large),
            verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.large),
        ) {
            Text(
                text = labels.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            WidgetLivePreview(
                title = labels.livePreview,
                theme = state.widgetTheme ?: state.theme,
            )

            val context = LocalContext.current

            ChoiceChipGroup(
                title = labels.themeLabel,
                options = themes,
                isSelected = { it == state.widgetTheme },
                enabled = !locked,
                label = { theme -> theme?.displayNameKey?.resourceId?.resolve(context) ?: labels.matchAppTheme },
                testTagFor = { SettingsTestTags.widgetThemeChip(it?.name ?: MATCH_APP_THEME_TAG) },
                onSelected = { onAction(WidgetThemeSelected(it)) },
            )
        }
    }
}

@Composable
private fun WidgetLivePreview(
    title: String,
    theme: ThemeModel,
    modifier: Modifier = Modifier,
) {
    val colors = theme.theme.toColorScheme(isSystemInDarkTheme = isSystemInDarkTheme())

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.small),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(PREVIEW_WIDGET_HEIGHT)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(TvManiacSpacing.small)
                .testTag(SettingsTestTags.WIDGET_APPEARANCE_PREVIEW_TEST_TAG),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium)
                    .background(colors.surface)
                    .padding(TvManiacSpacing.small),
                verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.xSmall),
            ) {
                WidgetPreviewRow(colors = colors)
                WidgetPreviewRow(colors = colors)
            }
        }
    }
}

@Composable
private fun WidgetPreviewRow(
    colors: ColorScheme,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(PREVIEW_POSTER_WIDTH)
                .height(PREVIEW_POSTER_HEIGHT)
                .clip(RoundedCornerShape(TvManiacSpacing.xxSmall))
                .background(colors.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Movie,
                contentDescription = null,
                tint = colors.onSurfaceVariant,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.xxxSmall)) {
            Box(
                modifier = Modifier
                    .width(112.dp)
                    .height(TvManiacSpacing.small)
                    .clip(RoundedCornerShape(TvManiacSpacing.xxxSmall))
                    .background(colors.onSurface),
            )
            Box(
                modifier = Modifier
                    .width(72.dp)
                    .height(TvManiacSpacing.xSmall)
                    .clip(RoundedCornerShape(TvManiacSpacing.xxxSmall))
                    .background(colors.onSurfaceVariant),
            )
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun WidgetAppearancePagePreview(
    @PreviewParameter(WidgetAppearancePreviewParameterProvider::class) state: SettingsState,
) {
    WidgetAppearancePage(
        state = state,
        onAction = {},
    )
}
