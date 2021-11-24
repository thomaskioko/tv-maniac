package com.thomaskioko.tvmaniac.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.rememberFlowWithLifecycle
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.settings.api.TvManiacPreferences.Theme

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {

    val themeState by rememberFlowWithLifecycle(viewModel.themeState)
        .collectAsState(initial = Theme.DARK)

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
            )
        },
        modifier = Modifier
            .statusBarsPadding(),
        content = { innerPadding ->
            SettingsList(
                theme = themeState,
                onThemeChanged = { viewModel.updateTheme(it) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    )
}

@Composable
fun SettingsList(
    theme: Theme,
    onThemeChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 2.dp, end = 16.dp)
    ) {
        item {
            ThemeSettingsItem(
                theme = theme,
                onThemeChanged = onThemeChanged
            )
            AboutSettingsItem()
        }
    }
}

@Composable
private fun ThemeSettingsItem(
    theme: Theme,
    onThemeChanged: (String) -> Unit,
) {

    val checkedState = when (theme) {
        Theme.LIGHT -> false
        Theme.DARK -> true
        Theme.SYSTEM -> true
    }

    val themeTitle = if (checkedState) stringResource(R.string.settings_title_theme_dark)
    else stringResource(R.string.settings_theme_title_light)

    SettingHeaderTitle(
        title = stringResource(R.string.settings_title_ui),
        modifier = Modifier.padding(start = 16.dp, top = 16.dp)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp),
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

        val resources = LocalContext.current.resources

        Switch(
            checked = checkedState,
            enabled = true,
            onCheckedChange = {
                when (it) {
                    true -> onThemeChanged(resources.getString(R.string.pref_theme_dark_value))
                    false -> onThemeChanged(resources.getString(R.string.pref_theme_light_value))
                }
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colors.secondaryVariant,
                checkedTrackColor = MaterialTheme.colors.secondaryVariant,
            ),
        )
    }

    SettingListDivider()
}

@Composable
private fun AboutSettingsItem() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        SettingHeaderTitle(title = stringResource(R.string.settings_title_info))

        SettingTitle(title = stringResource(R.string.settings_title_about))

        SettingDescription(description = stringResource(R.string.settings_about_description))
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
            .fillMaxWidth()
            .padding(bottom = 8.dp),
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
        modifier = Modifier.padding(horizontal = 14.dp),
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
            theme = Theme.DARK,
            onThemeChanged = {}
        )
    }
}
