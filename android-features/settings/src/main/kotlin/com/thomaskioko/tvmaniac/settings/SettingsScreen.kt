package com.thomaskioko.tvmaniac.settings

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.BasicDialog
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.datastore.api.Theme
import com.thomaskioko.tvmaniac.navigation.extensions.viewModel
import com.thomaskioko.tvmaniac.presentation.settings.ChangeThemeClicked
import com.thomaskioko.tvmaniac.presentation.settings.DimissThemeClicked
import com.thomaskioko.tvmaniac.presentation.settings.DismissTraktDialog
import com.thomaskioko.tvmaniac.presentation.settings.SettingsContent
import com.thomaskioko.tvmaniac.presentation.settings.SettingsState
import com.thomaskioko.tvmaniac.presentation.settings.ShowTraktDialog
import com.thomaskioko.tvmaniac.presentation.settings.ThemeSelected
import com.thomaskioko.tvmaniac.presentation.settings.TraktLogout
import com.thomaskioko.tvmaniac.resources.R
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias Settings = @Composable (
    onBackClicked: () -> Unit,
) -> Unit

@Inject
@Composable
fun Settings(
    viewModelFactory: () -> SettingsViewModel,
    @Assisted onBackClicked: () -> Unit,
) {
    SettingsScreen(
        viewModel = viewModel(factory = viewModelFactory),
        onBackClicked = onBackClicked,
    )
}

@Composable
internal fun SettingsScreen(
    onBackClicked: () -> Unit,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    val settingsState by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        state = settingsState,
        modifier = modifier,
        onBackClicked = onBackClicked,
        onThemeChanged = { viewModel.dispatch(ThemeSelected(it)) },
        onThemeClicked = { viewModel.dispatch(ChangeThemeClicked) },
        onDismissTheme = { viewModel.dispatch(DimissThemeClicked) },
        onLogoutClicked = { viewModel.dispatch(TraktLogout) },
        onLoginClicked = {
            viewModel.login()
            viewModel.dispatch(DismissTraktDialog)
        },
        onConnectClicked = { viewModel.dispatch(ShowTraktDialog) },
        onDismissDialogClicked = { viewModel.dispatch(DismissTraktDialog) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    onBackClicked: () -> Unit,
    state: SettingsState,
    onThemeChanged: (Theme) -> Unit,
    onThemeClicked: () -> Unit,
    onDismissTheme: () -> Unit,
    onConnectClicked: () -> Unit,
    onLoginClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
    modifier: Modifier = Modifier,
    onDismissDialogClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            TvManiacTopBar(
                title = stringResource(R.string.title_settings),
                showNavigationIcon = true,
                onBackClick = onBackClicked,
                modifier = Modifier,
            )
        },
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        content = { innerPadding ->

            when (state) {
                is SettingsContent -> {
                    SettingsList(
                        settingsContent = state,
                        onThemeChanged = onThemeChanged,
                        onThemeClicked = onThemeClicked,
                        onDismissTheme = onDismissTheme,
                        onLogoutClicked = onLogoutClicked,
                        onLoginClicked = onLoginClicked,
                        onConnectClicked = onConnectClicked,
                        onDismissDialogClicked = onDismissDialogClicked,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    )
                }
            }
        },
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
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            ThemeSettingsItem(
                settingsContent = settingsContent,
                onThemeSelected = onThemeChanged,
                onThemeClicked = onThemeClicked,
                onDismissTheme = onDismissTheme,
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
                onConnectClicked = onConnectClicked,
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

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
            traktUserName ?: traktFullName ?: "",
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
        Spacer(modifier = Modifier.height(8.dp))

        SettingHeaderTitle(title = stringResource(R.string.settings_title_trakt))

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (!traktUserPicUrl.isNullOrBlank()) {
                AsyncImageComposable(
                    model = traktUserPicUrl,
                    contentDescription = stringResource(
                        R.string.cd_profile_pic,
                        traktUserName ?: traktFullName ?: "",
                    ),
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.secondary, CircleShape),

                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Person,
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(48.dp),
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
                onDismissDialog = onDismissDialogClicked,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

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
            initialAlpha = 0.4f,
        ),
        exit = fadeOut(
            // Overwrites the default animation with tween
            animationSpec = tween(durationMillis = 250),
        ),
    ) {
        BasicDialog(
            dialogTitle = stringResource(id = R.string.settings_title_trakt_app),
            dialogMessage = stringResource(id = R.string.trakt_description),
            confirmButtonText = stringResource(id = R.string.login),
            dismissButtonText = stringResource(id = R.string.logout),
            onDismissDialog = onDismissDialog,
            confirmButtonClicked = onLoginClicked,
            dismissButtonClicked = onLogoutClicked,
        )
    }
}

@Composable
private fun ThemeSettingsItem(
    settingsContent: SettingsContent,
    onThemeSelected: (Theme) -> Unit,
    onThemeClicked: () -> Unit,
    onDismissTheme: () -> Unit,
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
            .padding(start = 16.dp, end = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        SettingHeaderTitle(
            title = stringResource(R.string.settings_title_ui),
            modifier = Modifier,
        )

        Spacer(modifier = Modifier.height(8.dp))

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
                    .size(48.dp),
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
                onThemeSelected = onThemeSelected,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

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
            initialAlpha = 0.4f,
        ),
        exit = fadeOut(
            // Overwrites the default animation with tween
            animationSpec = tween(durationMillis = 250),
        ),
    ) {
        DropdownMenu(
            expanded = isVisible,
            onDismissRequest = { onDismissTheme() },
            offset = DpOffset(16.dp, 32.dp),
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface),

        ) {
            ThemeMenuItem(
                theme = Theme.LIGHT,
                selectedTheme = selectedTheme,
                onThemeSelected = onThemeSelected,
                onDismissTheme = onDismissTheme,
            )

            ThemeMenuItem(
                theme = Theme.DARK,
                selectedTheme = selectedTheme,
                onThemeSelected = onThemeSelected,
                onDismissTheme = onDismissTheme,
            )

            ThemeMenuItem(
                theme = Theme.SYSTEM,
                selectedTheme = selectedTheme,
                onThemeSelected = onThemeSelected,
                onDismissTheme = onDismissTheme,
            )
        }
    }
}

@Composable
private fun ThemeMenuItem(
    theme: Theme,
    selectedTheme: Theme,
    onThemeSelected: (Theme) -> Unit,
    onDismissTheme: () -> Unit,
) {
    val themeTitle = when (theme) {
        Theme.LIGHT -> "Light Theme"
        Theme.DARK -> "Dark Theme"
        Theme.SYSTEM -> "System Theme"
    }
    DropdownMenuItem(
        onClick = {
            onThemeSelected(theme)
            onDismissTheme()
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = themeTitle,
                modifier = Modifier
                    .weight(1f),
            )

            RadioButton(
                selected = selectedTheme == theme,
                onClick = {
                    onThemeSelected(theme)
                    onDismissTheme()
                },
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
            .padding(start = 16.dp, end = 16.dp),
    ) {
        SettingHeaderTitle(title = stringResource(R.string.settings_title_info))

        Spacer(modifier = Modifier.height(8.dp))

        TitleItem(title = stringResource(R.string.settings_title_about))

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = stringResource(R.string.settings_about_description),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        ListDivider()
    }
}

@Composable
fun SettingHeaderTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.secondary,
        ),
        modifier = modifier
            .fillMaxWidth(),
    )
}

@Composable
fun TitleItem(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier,
    )
}

@Composable
fun SettingDescription(
    description: String,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = modifier,
        )
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
private fun SettingsScreenPreview(
    @PreviewParameter(SettingsPreviewParameterProvider::class)
    state: SettingsState,
) {
    TvManiacTheme {
        Surface {
            SettingsScreen(
                state = state,
                onThemeChanged = {},
                onThemeClicked = {},
                onDismissTheme = {},
                onLogoutClicked = {},
                onLoginClicked = {},
                onDismissDialogClicked = {},
                onConnectClicked = {},
                onBackClicked = {},
            )
        }
    }
}
