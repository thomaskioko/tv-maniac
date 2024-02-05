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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.BasicDialog
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.presentation.settings.ChangeThemeClicked
import com.thomaskioko.tvmaniac.presentation.settings.DismissThemeClicked
import com.thomaskioko.tvmaniac.presentation.settings.DismissTraktDialog
import com.thomaskioko.tvmaniac.presentation.settings.SettingsActions
import com.thomaskioko.tvmaniac.presentation.settings.SettingsPresenter
import com.thomaskioko.tvmaniac.presentation.settings.SettingsState
import com.thomaskioko.tvmaniac.presentation.settings.ShowTraktDialog
import com.thomaskioko.tvmaniac.presentation.settings.ThemeSelected
import com.thomaskioko.tvmaniac.presentation.settings.TraktLoginClicked
import com.thomaskioko.tvmaniac.presentation.settings.TraktLogoutClicked
import com.thomaskioko.tvmaniac.presentation.settings.UserInfo
import com.thomaskioko.tvmaniac.resources.R

@Composable
fun SettingsScreen(
  presenter: SettingsPresenter,
  modifier: Modifier = Modifier,
) {
  val state by presenter.value.subscribeAsState()
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
  Scaffold(
    topBar = {
      TvManiacTopBar(
        title = {
          Text(
            text = stringResource(R.string.title_settings),
            style =
              MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
              ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
          )
        },
        modifier = Modifier,
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

      SettingsScreen(
        userInfo = state.userInfo,
        appTheme = state.appTheme,
        showPopup = state.showthemePopup,
        showTraktDialog = state.showTraktDialog,
        isLoading = state.isLoading,
        onAction = onAction,
        modifier = Modifier.fillMaxSize().padding(innerPadding),
      )
    },
  )
}

@Composable
fun SettingsScreen(
  userInfo: UserInfo?,
  appTheme: AppTheme,
  showPopup: Boolean,
  showTraktDialog: Boolean,
  isLoading: Boolean,
  onAction: (SettingsActions) -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    modifier = modifier,
  ) {
    item { Spacer(modifier = Modifier.height(16.dp)) }

    item {
      TraktProfileSettingsItem(
        showTraktDialog = showTraktDialog,
        isLoading = isLoading,
        loggedIn = userInfo != null,
        traktUserName = userInfo?.userName,
        traktFullName = userInfo?.fullName,
        traktUserPicUrl = userInfo?.userPicUrl,
        onAction = onAction,
      )
    }

    item {
      SettingsThemeItem(
        showPopup = showPopup,
        appTheme = appTheme,
        onThemeSelected = { onAction(ThemeSelected(it)) },
        onThemeClicked = { onAction(ChangeThemeClicked) },
        onDismissTheme = { onAction(DismissThemeClicked) },
      )
    }

    item { Spacer(modifier = Modifier.height(16.dp)) }

    item { AboutSettingsItem() }
  }
}

@Composable
private fun TraktProfileSettingsItem(
  isLoading: Boolean,
  showTraktDialog: Boolean,
  loggedIn: Boolean,
  traktUserName: String?,
  traktFullName: String?,
  traktUserPicUrl: String?,
  onAction: (SettingsActions) -> Unit,
) {
  val titleId =
    if (loggedIn) {
      stringResource(
        R.string.settings_title_disconnect_trakt,
        traktUserName ?: traktFullName ?: "",
      )
    } else {
      stringResource(R.string.settings_title_connect_trakt)
    }

  Column(
    modifier =
      Modifier.fillMaxWidth()
        .clickable { onAction(ShowTraktDialog) }
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
        if (isLoading) {
          CircularProgressIndicator(
            modifier = Modifier.padding(end = 16.dp).size(48.dp),
            color = MaterialTheme.colorScheme.secondary,
          )
        } else {
          AsyncImageComposable(
            model = traktUserPicUrl,
            contentDescription =
              stringResource(
                R.string.cd_profile_pic,
                traktUserName ?: traktFullName ?: "",
              ),
            modifier =
              Modifier.padding(end = 16.dp)
                .size(48.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.secondary, CircleShape),
          )
        }
      } else {
        Icon(
          imageVector = Icons.Filled.Person,
          tint = MaterialTheme.colorScheme.secondary,
          contentDescription = null,
          modifier = Modifier.padding(end = 16.dp).size(48.dp),
        )
      }

      Column(
        modifier = Modifier.weight(1f),
      ) {
        TitleItem(titleId)
        SettingDescription(stringResource(R.string.trakt_description))
      }

      TrackDialog(
        loggedIn = loggedIn,
        isVisible = showTraktDialog,
        onLoginClicked = { onAction(TraktLoginClicked) },
        onLogoutClicked = { onAction(TraktLogoutClicked) },
        onDismissDialog = { onAction(DismissTraktDialog) },
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    ListDivider()
  }
}

@Composable
fun TrackDialog(
  isVisible: Boolean,
  loggedIn: Boolean,
  onLoginClicked: () -> Unit,
  onLogoutClicked: () -> Unit,
  onDismissDialog: () -> Unit,
) {
  val title =
    if (loggedIn) {
      stringResource(id = R.string.trakt_dialog_logout_title)
    } else {
      stringResource(id = R.string.trakt_dialog_login_title)
    }

  val message =
    if (loggedIn) {
      stringResource(id = R.string.trakt_dialog_logout_message)
    } else {
      stringResource(id = R.string.trakt_dialog_login_message)
    }
  AnimatedVisibility(
    visible = isVisible,
    enter =
      fadeIn(
        initialAlpha = 0.4f,
      ),
    exit =
      fadeOut(
        // Overwrites the default animation with tween
        animationSpec = tween(durationMillis = 250),
      ),
  ) {
    BasicDialog(
      dialogTitle = title,
      dialogMessage = message,
      confirmButtonText = stringResource(id = R.string.login),
      dismissButtonText = stringResource(id = R.string.logout),
      onDismissDialog = onDismissDialog,
      confirmButtonClicked = onLoginClicked,
      dismissButtonClicked = onLogoutClicked,
      enableConfirmButton = !loggedIn,
      enableDismissButton = loggedIn,
    )
  }
}

@Composable
private fun SettingsThemeItem(
  appTheme: AppTheme,
  showPopup: Boolean,
  onThemeSelected: (AppTheme) -> Unit,
  onThemeClicked: () -> Unit,
  onDismissTheme: () -> Unit,
) {
  val appThemeTitle =
    when (appTheme) {
      AppTheme.LIGHT_THEME -> stringResource(R.string.settings_title_theme_dark)
      AppTheme.DARK_THEME -> stringResource(R.string.settings_title_theme_light)
      AppTheme.SYSTEM_THEME -> stringResource(R.string.settings_title_theme_system)
    }

  Column(
    modifier =
      Modifier.fillMaxWidth().clickable { onThemeClicked() }.padding(start = 16.dp, end = 16.dp),
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
        imageVector = Icons.Filled.Palette,
        tint = MaterialTheme.colorScheme.secondary,
        contentDescription = null,
        modifier = Modifier.padding(end = 16.dp).size(48.dp),
      )

      Column(
        modifier = Modifier.padding(end = 8.dp, bottom = 8.dp).weight(1f),
      ) {
        TitleItem(appThemeTitle)
        SettingDescription(stringResource(R.string.settings_theme_description))
      }

      ThemeMenu(
        isVisible = showPopup,
        selectedAppTheme = appTheme,
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
  selectedAppTheme: AppTheme,
  onDismissTheme: () -> Unit,
  onThemeSelected: (AppTheme) -> Unit,
) {
  AnimatedVisibility(
    visible = isVisible,
    enter =
      fadeIn(
        // Overwrites the initial value of alpha to 0.4f for fade in, 0 by default
        initialAlpha = 0.4f,
      ),
    exit =
      fadeOut(
        // Overwrites the default animation with tween
        animationSpec = tween(durationMillis = 250),
      ),
  ) {
    DropdownMenu(
      expanded = isVisible,
      onDismissRequest = { onDismissTheme() },
      offset = DpOffset(16.dp, 32.dp),
      modifier = Modifier.background(MaterialTheme.colorScheme.surface),
    ) {
      ThemeMenuItem(
        appTheme = AppTheme.LIGHT_THEME,
        selectedAppTheme = selectedAppTheme,
        onThemeSelected = onThemeSelected,
        onDismissTheme = onDismissTheme,
      )

      ThemeMenuItem(
        appTheme = AppTheme.DARK_THEME,
        selectedAppTheme = selectedAppTheme,
        onThemeSelected = onThemeSelected,
        onDismissTheme = onDismissTheme,
      )

      ThemeMenuItem(
        appTheme = AppTheme.SYSTEM_THEME,
        selectedAppTheme = selectedAppTheme,
        onThemeSelected = onThemeSelected,
        onDismissTheme = onDismissTheme,
      )
    }
  }
}

@Composable
private fun ThemeMenuItem(
  appTheme: AppTheme,
  selectedAppTheme: AppTheme,
  onThemeSelected: (AppTheme) -> Unit,
  onDismissTheme: () -> Unit,
) {
  val appThemeTitle =
    when (appTheme) {
      AppTheme.LIGHT_THEME -> "Light Theme"
      AppTheme.DARK_THEME -> "Dark Theme"
      AppTheme.SYSTEM_THEME -> "System Theme"
    }
  DropdownMenuItem(
    onClick = {
      onThemeSelected(appTheme)
      onDismissTheme()
    },
    text = {
      Row(
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = appThemeTitle,
          modifier = Modifier.weight(1f),
        )

        RadioButton(
          selected = selectedAppTheme == appTheme,
          colors =
            RadioButtonDefaults.colors(
              selectedColor = MaterialTheme.colorScheme.secondary,
            ),
          onClick = {
            onThemeSelected(appTheme)
            onDismissTheme()
          },
        )
      }
    },
  )
}

@Composable
private fun AboutSettingsItem() {
  Column(
    modifier = Modifier.fillMaxSize().clickable {}.padding(start = 16.dp, end = 16.dp),
  ) {
    SettingHeaderTitle(title = stringResource(R.string.settings_title_info))

    Spacer(modifier = Modifier.height(8.dp))

    TitleItem(title = stringResource(R.string.settings_title_about))

    Text(
      text = stringResource(R.string.settings_about_description),
      style = MaterialTheme.typography.bodyMedium,
      fontWeight = FontWeight.Normal,
    )

    Spacer(modifier = Modifier.height(8.dp))

    ListDivider()
  }
}

@Composable
fun SettingHeaderTitle(title: String, modifier: Modifier = Modifier) {
  Text(
    text = title,
    style =
      MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.colorScheme.secondary,
      ),
    modifier = modifier.fillMaxWidth(),
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
  Text(
    text = description,
    style = MaterialTheme.typography.bodyMedium,
    modifier = modifier,
    fontWeight = FontWeight.Normal,
  )
}

/** Full-width divider with padding for settings items */
@Composable
private fun ListDivider() {
  Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
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
