package com.thomaskioko.tvmaniac.settings.ui

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.thomaskioko.tvmaniac.compose.components.BasicDialog
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_back
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_settings_image_quality
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_settings_image_quality_high
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_settings_image_quality_high_description
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_settings_image_quality_low
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_settings_image_quality_low_description
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_settings_image_quality_medium
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_settings_image_quality_medium_description
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_settings_privacy_policy
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_settings_section_appearance
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_settings_trakt_dialog_button_secondary
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_settings_youtube
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_settings_youtube_description
import com.thomaskioko.tvmaniac.i18n.MR.strings.logout
import com.thomaskioko.tvmaniac.i18n.MR.strings.settings_about_section_title
import com.thomaskioko.tvmaniac.i18n.MR.strings.settings_theme_selector_subtitle
import com.thomaskioko.tvmaniac.i18n.MR.strings.settings_title_about
import com.thomaskioko.tvmaniac.i18n.MR.strings.settings_title_disconnect_trakt
import com.thomaskioko.tvmaniac.i18n.MR.strings.settings_title_info
import com.thomaskioko.tvmaniac.i18n.MR.strings.settings_title_trakt
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_settings
import com.thomaskioko.tvmaniac.i18n.MR.strings.trakt_description
import com.thomaskioko.tvmaniac.i18n.MR.strings.trakt_dialog_logout_message
import com.thomaskioko.tvmaniac.i18n.MR.strings.trakt_dialog_logout_title
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.settings.presenter.BackClicked
import com.thomaskioko.tvmaniac.settings.presenter.DismissAboutDialog
import com.thomaskioko.tvmaniac.settings.presenter.DismissTraktDialog
import com.thomaskioko.tvmaniac.settings.presenter.ImageQualitySelected
import com.thomaskioko.tvmaniac.settings.presenter.SettingsActions
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPresenter
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.settings.presenter.ShowAboutDialog
import com.thomaskioko.tvmaniac.settings.presenter.ShowTraktDialog
import com.thomaskioko.tvmaniac.settings.presenter.ThemeSelected
import com.thomaskioko.tvmaniac.settings.presenter.TraktLogoutClicked
import com.thomaskioko.tvmaniac.settings.presenter.YoutubeToggled

private const val GITHUB_URL = "https://github.com/c0de-wizard/tv-maniac"
private const val PRIVACY_POLICY_URL = "https://github.com/c0de-wizard/tv-maniac"

@Composable
public fun SettingsScreen(
    presenter: SettingsPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    SettingsScreen(
        modifier = modifier,
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = presenter::dispatch,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    state: SettingsState,
    snackbarHostState: SnackbarHostState,
    onAction: (SettingsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TvManiacTopBar(
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .clickable(onClick = { onAction(BackClicked) })
                            .padding(16.dp),
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = cd_back.resolve(context),
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                },
                title = {
                    Text(
                        text = title_settings.resolve(context),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                    )
                },
                modifier = Modifier,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier,
        content = { innerPadding ->
            LaunchedEffect(key1 = state.errorMessage) {
                if (state.errorMessage != null) {
                    snackbarHostState.showSnackbar(
                        message = state.errorMessage!!,
                        duration = SnackbarDuration.Short,
                    )
                }
            }

            SettingsContent(
                state = state,
                onAction = onAction,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        },
    )

    if (state.showAboutDialog) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { onAction(DismissAboutDialog) },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            AboutSheetContent(
                onGitHubClick = { openInCustomTab(context, GITHUB_URL) },
            )
        }
    }
}

@Composable
private fun SettingsContent(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    LazyColumn(modifier = modifier) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            SectionHeader(
                title = label_settings_section_appearance.resolve(context),
                icon = Icons.Filled.Palette,
                subtitle = settings_theme_selector_subtitle.resolve(context),
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            ThemeSelectorSection(
                selectedTheme = state.theme,
                onThemeSelected = { onAction(ThemeSelected(it)) },
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            ImageQualitySection(
                imageQuality = state.imageQuality,
                onQualitySelected = { onAction(ImageQualitySelected(it)) },
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            SettingsToggleItem(
                icon = Icons.Filled.Tv,
                title = label_settings_youtube.resolve(context),
                subtitle = label_settings_youtube_description.resolve(context),
                checked = state.openTrailersInYoutube,
                onCheckedChange = { onAction(YoutubeToggled(it)) },
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            SectionHeader(title = settings_title_info.resolve(context))
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            SettingsClickableItem(
                icon = Icons.Filled.Info,
                title = settings_about_section_title.resolve(context),
                subtitle = settings_title_about.resolve(context),
                onClick = { onAction(ShowAboutDialog) },
            )
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            SettingsClickableItem(
                icon = Icons.Filled.Security,
                title = label_settings_privacy_policy.resolve(context),
                onClick = { openInCustomTab(context, PRIVACY_POLICY_URL) },
            )
        }

        if (state.isAuthenticated) {
            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                SectionHeader(title = settings_title_trakt.resolve(context))
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                TraktAccountSection(
                    showTraktDialog = state.showTraktDialog,
                    onAction = onAction,
                )
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    subtitle: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ImageQualitySection(
    imageQuality: ImageQuality,
    onQualitySelected: (ImageQuality) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Image,
                tint = MaterialTheme.colorScheme.secondary,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label_settings_image_quality.resolve(context),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = getQualityDescriptionString(imageQuality, context),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(start = 40.dp),
        ) {
            ImageQualityChip(
                label = label_settings_image_quality_high.resolve(context),
                isSelected = imageQuality == ImageQuality.HIGH,
                onClick = { onQualitySelected(ImageQuality.HIGH) },
            )
            ImageQualityChip(
                label = label_settings_image_quality_medium.resolve(context),
                isSelected = imageQuality == ImageQuality.MEDIUM,
                onClick = { onQualitySelected(ImageQuality.MEDIUM) },
            )
            ImageQualityChip(
                label = label_settings_image_quality_low.resolve(context),
                isSelected = imageQuality == ImageQuality.LOW,
                onClick = { onQualitySelected(ImageQuality.LOW) },
            )
        }
    }
}

@Composable
private fun ImageQualityChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
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

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )

        Spacer(modifier = Modifier.width(16.dp))

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

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.secondary,
                checkedTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurface,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                uncheckedBorderColor = MaterialTheme.colorScheme.outline,
            ),
        )
    }
}

@Composable
private fun SettingsClickableItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun TraktAccountSection(
    showTraktDialog: Boolean,
    onAction: (SettingsActions) -> Unit,
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAction(ShowTraktDialog) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(settings_title_disconnect_trakt.resourceId, ""),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = trakt_description.resolve(context),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    LogoutDialog(
        isVisible = showTraktDialog,
        onLogoutClicked = { onAction(TraktLogoutClicked) },
        onDismissDialog = { onAction(DismissTraktDialog) },
    )
}

@Composable
private fun LogoutDialog(
    isVisible: Boolean,
    onLogoutClicked: () -> Unit,
    onDismissDialog: () -> Unit,
) {
    val context = LocalContext.current

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(initialAlpha = 0.4f),
        exit = fadeOut(animationSpec = tween(durationMillis = 250)),
    ) {
        BasicDialog(
            dialogTitle = trakt_dialog_logout_title.resolve(context),
            dialogMessage = trakt_dialog_logout_message.resolve(context),
            confirmButtonText = logout.resolve(context),
            dismissButtonText = label_settings_trakt_dialog_button_secondary.resolve(context),
            onDismissDialog = onDismissDialog,
            confirmButtonClicked = onLogoutClicked,
            dismissButtonClicked = onDismissDialog,
            enableConfirmButton = true,
            enableDismissButton = true,
        )
    }
}

private fun openInCustomTab(context: Context, url: String) {
    val customTabsIntent = CustomTabsIntent.Builder().build()
    customTabsIntent.launchUrl(context, url.toUri())
}

private fun getQualityDescriptionString(quality: ImageQuality, context: Context): String {
    return when (quality) {
        ImageQuality.HIGH -> label_settings_image_quality_high_description.resolve(context)
        ImageQuality.MEDIUM -> label_settings_image_quality_medium_description.resolve(context)
        ImageQuality.LOW -> label_settings_image_quality_low_description.resolve(context)
    }
}

@ThemePreviews
@Composable
private fun SettingsScreenPreview(
    @PreviewParameter(SettingsPreviewParameterProvider::class) state: SettingsState,
) {
    TvManiacTheme {
        Surface {
            SettingsScreen(
                state = state,
                snackbarHostState = SnackbarHostState(),
                onAction = {},
            )
        }
    }
}
