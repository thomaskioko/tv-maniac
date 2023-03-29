package com.thomaskioko.tvmaniac.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.BasicDialog
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.extensions.iconButtonBackgroundScrim
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.datastore.api.Theme
import com.thomaskioko.tvmaniac.resources.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    navigateUp: () -> Unit
) {

    val settingsState by viewModel.state.collectAsStateWithLifecycle()

    val loginLauncher = rememberLauncherForActivityResult(
        viewModel.buildLoginActivityResult()
    ) { result ->
        if (result != null) {
            viewModel.onLoginResult(result)
        }
    }

    Scaffold(
        topBar = {
            TvManiacTopBar(
                title = stringResource(R.string.title_settings),
                showNavigationIcon = true,
                onBackClick = navigateUp,
                modifier = Modifier.iconButtonBackgroundScrim()
            )
        },
        modifier = Modifier
            .statusBarsPadding(),
        content = { innerPadding ->

            when (settingsState) {
                is SettingsContent -> {
                    SettingsList(
                        settingsContent = settingsState as SettingsContent,
                        onThemeChanged = { viewModel.dispatch(ThemeSelected(it)) },
                        onThemeClicked = { viewModel.dispatch(ChangeThemeClicked) },
                        onDismissTheme = { viewModel.dispatch(DimissThemeClicked) },
                        onLogoutClicked = { viewModel.dispatch(TraktLogout) },
                        onLoginClicked = {
                            loginLauncher.launch(Unit)
                            viewModel.dispatch(DismissTraktDialog)
                        },
                        onConnectClicked = { viewModel.dispatch(ShowTraktDialog) },
                        onDismissDialogClicked = { viewModel.dispatch(DismissTraktDialog) },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    )
                }
            }

        }
    )
}

@Composable
fun SettingsList(
    settingsContent: SettingsContent,
    onThemeChanged: (Theme) -> Unit,
    onThemeClicked: () -> Unit,
    onDismissTheme: () -> Unit,
    onConnectClicked: () -> Unit,
    onLoginClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
    onDismissDialogClicked: () -> Unit,
    modifier: Modifier = Modifier,

    ) {
    LazyColumn(
        modifier = modifier,
    ) {

        item { ColumnSpacer(value = 16) }

        item {
            ThemeSettingsItem(
                settingsContent = settingsContent,
                onThemeSelected = onThemeChanged,
                onThemeClicked = onThemeClicked,
                onDismissTheme = onDismissTheme
            )
        }

        item {
            TraktProfileSettingsItem(
                showTraktDialog = settingsContent.showTraktDialog,
                loggedIn = settingsContent.loggedIn,
                traktUserName = settingsContent.traktUserName,
                traktFullName = settingsContent.traktFullName,
                traktUserPicUrl = settingsContent.traktUserPicUrl,
                onLoginClicked = onLoginClicked,
                onLogoutClicked = onLogoutClicked,
                onDismissDialogClicked = onDismissDialogClicked,
                onConnectClicked = onConnectClicked
            )
        }

        item { ColumnSpacer(value = 16) }

        item { AboutSettingsItem() }
    }
}

@Composable
private fun TraktProfileSettingsItem(
    showTraktDialog: Boolean,
    loggedIn: Boolean,
    traktUserName: String?,
    traktFullName: String?,
    traktUserPicUrl: String?,
    onConnectClicked: () -> Unit,
    onLoginClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
    onDismissDialogClicked: () -> Unit,
) {
    val titleId = if (loggedIn) {
        stringResource(
            R.string.settings_title_disconnect_trakt,
            traktUserName ?: traktFullName ?: ""
        )
    } else {
        stringResource(R.string.settings_title_connect_trakt)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onConnectClicked() }
            .padding(start = 16.dp, end = 16.dp),
    ) {
        ColumnSpacer(value = 8)

        SettingHeaderTitle(title = stringResource(R.string.settings_title_trakt))

        ColumnSpacer(value = 8)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (!traktUserPicUrl.isNullOrBlank()) {
                AsyncImageComposable(
                    model = traktUserPicUrl,
                    contentDescription = stringResource(
                        R.string.cd_profile_pic,
                        traktUserName ?: traktFullName ?: ""
                    ),
                    modifier = Modifier
                        .padding(top = 64.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.secondary, CircleShape)

                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Person,
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(28.dp)
                )
            }


            Column(
                modifier = Modifier
                    .weight(1f),
            ) {
                TitleItem(titleId)
                SettingDescription(stringResource(R.string.trakt_description))
            }

            TrackDialog(
                isVisible = showTraktDialog,
                onLoginClicked = onLoginClicked,
                onLogoutClicked = onLogoutClicked,
                onDismissDialog = onDismissDialogClicked
            )
        }

        ColumnSpacer(value = 8)

        ListDivider()
    }
}

@Composable
fun TrackDialog(
    isVisible: Boolean,
    onLoginClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
    onDismissDialog: () -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(
            initialAlpha = 0.4f
        ),
        exit = fadeOut(
            // Overwrites the default animation with tween
            animationSpec = tween(durationMillis = 250)
        )
    ) {

        BasicDialog(
            dialogTitle = stringResource(id = R.string.settings_title_trakt_app),
            dialogMessage = stringResource(id = R.string.trakt_description),
            confirmButtonText = stringResource(id = R.string.login),
            dismissButtonText = stringResource(id = R.string.logout),
            onDismissDialog = onDismissDialog,
            confirmButtonClicked = onLoginClicked,
            dismissButtonClicked = onLogoutClicked
        )
    }
}

@Composable
private fun ThemeSettingsItem(
    settingsContent: SettingsContent,
    onThemeSelected: (Theme) -> Unit,
    onThemeClicked: () -> Unit,
    onDismissTheme: () -> Unit
) {

    val themeTitle = when (settingsContent.theme) {
        Theme.LIGHT -> stringResource(R.string.settings_title_theme_dark)
        Theme.DARK -> stringResource(R.string.settings_title_theme_light)
        Theme.SYSTEM -> stringResource(R.string.settings_title_theme_system)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onThemeClicked() }
            .padding(start = 16.dp, end = 16.dp)
    ) {

        ColumnSpacer(value = 8)

        SettingHeaderTitle(
            title = stringResource(R.string.settings_title_ui),
            modifier = Modifier
        )

        ColumnSpacer(value = 8)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_palette_24),
                tint = MaterialTheme.colorScheme.secondary,
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
                TitleItem(themeTitle)
                SettingDescription(stringResource(R.string.settings_theme_description))
            }

            ThemeMenu(
                isVisible = settingsContent.showPopup,
                selectedTheme = settingsContent.theme,
                onDismissTheme = onDismissTheme,
                onThemeSelected = onThemeSelected
            )
        }

        ColumnSpacer(value = 8)

        ListDivider()
    }
}

@Composable
private fun ThemeMenu(
    isVisible: Boolean,
    selectedTheme: Theme,
    onDismissTheme: () -> Unit,
    onThemeSelected: (Theme) -> Unit,
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
                .background(MaterialTheme.colorScheme.surface)

        ) {
            ThemeMenuItem(
                theme = Theme.LIGHT,
                selectedTheme = selectedTheme,
                onThemeSelected = onThemeSelected,
                onDismissTheme = onDismissTheme
            )

            ThemeMenuItem(
                theme =  Theme.DARK,
                selectedTheme = selectedTheme,
                onThemeSelected = onThemeSelected,
                onDismissTheme = onDismissTheme
            )

            ThemeMenuItem(
                theme = Theme.SYSTEM,
                selectedTheme = selectedTheme,
                onThemeSelected = onThemeSelected,
                onDismissTheme = onDismissTheme
            )
        }
    }
}

@Composable
private fun ThemeMenuItem(
    theme: Theme,
    selectedTheme: Theme,
    onThemeSelected: (Theme) -> Unit,
    onDismissTheme: () -> Unit
) {

    val themeTitle = when(theme){
        Theme.LIGHT -> "Light Theme"
        Theme.DARK -> "Dark Theme"
        Theme.SYSTEM -> "System Theme"
    }
    DropdownMenuItem(
        onClick = {
            onThemeSelected(theme)
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
                selected = selectedTheme == theme,
                onClick = {
                    onThemeSelected(theme)
                    onDismissTheme()
                }
            )
        }
    }
}

@Composable
private fun AboutSettingsItem() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable { }
            .padding(start = 16.dp, end = 16.dp)
    ) {
        SettingHeaderTitle(title = stringResource(R.string.settings_title_info))

        ColumnSpacer(value = 8)

        TitleItem(title = stringResource(R.string.settings_title_about))

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = stringResource(R.string.settings_about_description),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        ColumnSpacer(value = 8)

        ListDivider()
    }
}

@Composable
fun SettingHeaderTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.secondary
        ),
        modifier = modifier
            .fillMaxWidth(),
    )
}

@Composable
fun TitleItem(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium)
}

@Composable
fun SettingDescription(description: String) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(description, style = MaterialTheme.typography.bodyMedium)
    }
}

/**
 * Full-width divider with padding for settings items
 */
@Composable
private fun ListDivider() {
    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
}

@ThemePreviews
@Composable
fun SettingsPropertyPreview() {
    TvManiacTheme {
        Surface {
            SettingsList(
                settingsContent = SettingsContent.EMPTY,
                onThemeChanged = {},
                onThemeClicked = {},
                onDismissTheme = {},
                onLogoutClicked = {},
                onLoginClicked = {},
                onDismissDialogClicked = {},
                onConnectClicked = {}
            )
        }
    }
}
