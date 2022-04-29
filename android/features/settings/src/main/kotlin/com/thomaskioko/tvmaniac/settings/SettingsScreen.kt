package com.thomaskioko.tvmaniac.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.rememberFlowWithLifecycle
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.util.iconButtonBackgroundScrim
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.shared.persistance.SettingsActions
import com.thomaskioko.tvmaniac.shared.persistance.SettingsState
import com.thomaskioko.tvmaniac.shared.persistance.Theme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    navigateUp: () -> Unit
) {

    val themeState by rememberFlowWithLifecycle(viewModel.observeState())
        .collectAsState(initial = SettingsState.DEFAULT)

    Scaffold(
        topBar = {
            TvManiacTopBar(
                title = {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(
                            text = stringResource(R.string.title_settings),
                            color = MaterialTheme.colors.onSurface
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = navigateUp,
                        modifier = Modifier.iconButtonBackgroundScrim()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.background
            )
        },
        modifier = Modifier
            .statusBarsPadding(),
        content = { innerPadding ->
            SettingsList(
                settingsState = themeState,
                onThemeChanged = { viewModel.dispatch(SettingsActions.ThemeSelected(it)) },
                onThemeClicked = { viewModel.dispatch(SettingsActions.ThemeClicked) },
                onDismissTheme = { viewModel.dispatch(SettingsActions.ThemeClicked) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    )
}

@Composable
fun SettingsList(
    settingsState: SettingsState,
    onThemeChanged: (String) -> Unit,
    onThemeClicked: () -> Unit,
    onDismissTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            ThemeSettingsItem(
                settingsState = settingsState,
                onThemeSelected = onThemeChanged,
                onThemeClicked = onThemeClicked,
                onDismissTheme = onDismissTheme
            )
        }

        item { ColumnSpacer(value = 16) }

        item { AboutSettingsItem() }
    }
}

@Composable
private fun ThemeSettingsItem(
    settingsState: SettingsState,
    onThemeSelected: (String) -> Unit,
    onThemeClicked: () -> Unit,
    onDismissTheme: () -> Unit
) {

    val themeTitle = when (settingsState.theme) {
        Theme.LIGHT -> stringResource(R.string.settings_title_theme_dark)
        Theme.DARK -> stringResource(R.string.settings_title_theme_light)
        Theme.SYSTEM -> stringResource(R.string.settings_title_theme_system)
    }

    ColumnSpacer(value = 16)

    SettingHeaderTitle(
        title = stringResource(R.string.settings_title_ui),
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onThemeClicked() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_palette_24),
            tint = MaterialTheme.colors.secondary,
            contentDescription = null,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(28.dp)
        )

        Column(
            modifier = Modifier
                .padding(end = 8.dp, bottom = 8.dp)
                .weight(1f),
        ) {
            SettingTitle(themeTitle)
            SettingDescription(stringResource(R.string.settings_theme_description))
        }

        ThemeMenu(
            isVisible = settingsState.showPopup,
            theme = settingsState.theme,
            onDismissTheme = onDismissTheme,
            onThemeSelected = onThemeSelected
        )
    }

    SettingListDivider()
}

@Composable
private fun ThemeMenu(
    isVisible: Boolean,
    onDismissTheme: () -> Unit,
    onThemeSelected: (String) -> Unit,
    theme: Theme,
) {

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(
            // Overwrites the initial value of alpha to 0.4f for fade in, 0 by default
            initialAlpha = 0.4f
        ),
        exit = fadeOut(
            // Overwrites the default animation with tween
            animationSpec = tween(durationMillis = 250)
        )
    ) {

        DropdownMenu(
            expanded = isVisible,
            onDismissRequest = { onDismissTheme() },
            offset = DpOffset(16.dp, 32.dp),
            modifier = Modifier
                .background(MaterialTheme.colors.surface)

        ) {
            ThemeMenuItem(
                themeTitle = "Light",
                themeName = Theme.LIGHT.name,
                theme,
                onThemeSelected,
                onDismissTheme
            )

            ThemeMenuItem(
                themeTitle = "Dark",
                themeName = Theme.DARK.name,
                theme,
                onThemeSelected,
                onDismissTheme
            )

            ThemeMenuItem(
                themeTitle = "Use system default",
                themeName = Theme.SYSTEM.name,
                theme,
                onThemeSelected,
                onDismissTheme
            )
        }
    }
}

@Composable
private fun ThemeMenuItem(
    themeTitle: String,
    themeName: String,
    theme: Theme,
    onThemeSelected: (String) -> Unit,
    onDismissTheme: () -> Unit
) {
    DropdownMenuItem(
        onClick = {
            onThemeSelected(themeName.lowercase())
            onDismissTheme()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = themeTitle,
                modifier = Modifier
                    .weight(1f)
            )

            RadioButton(
                selected = theme.name == themeName,
                onClick = {
                    onThemeSelected(themeName.lowercase())
                    onDismissTheme()
                }
            )
        }
    }
}

@Composable
private fun AboutSettingsItem() {

    SettingHeaderTitle(title = stringResource(R.string.settings_title_info))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable { }
    ) {

        ColumnSpacer(value = 8)

        SettingTitle(title = stringResource(R.string.settings_title_about))

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = stringResource(R.string.settings_about_description),
                style = MaterialTheme.typography.body2,
            )
        }

        ColumnSpacer(value = 8)
    }

    SettingListDivider()
}

@Composable
fun SettingHeaderTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.body2.copy(
            color = MaterialTheme.colors.secondary
        ),
        modifier = modifier
            .fillMaxWidth(),
    )
}

@Composable
fun SettingTitle(title: String) {
    Text(title, style = MaterialTheme.typography.subtitle1)
}

@Composable
fun SettingDescription(description: String) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(description, style = MaterialTheme.typography.body2)
    }
}

/**
 * Full-width divider with padding for settings items
 */
@Composable
private fun SettingListDivider() {
    Divider(
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
    )
}

@Preview(
    name = "Settings List",
    showSystemUi = true
)
@Composable
fun SettingsPropertyPreview() {
    TvManiacTheme {
        SettingsList(
            settingsState = SettingsState.DEFAULT,
            onThemeChanged = {},
            onThemeClicked = {},
            onDismissTheme = {}
        )
    }
}
