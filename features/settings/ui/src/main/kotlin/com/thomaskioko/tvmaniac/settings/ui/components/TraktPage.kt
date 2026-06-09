package com.thomaskioko.tvmaniac.settings.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.android.feature.settings.R
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacAlertDialog
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_settings_trakt_dialog_button_secondary
import com.thomaskioko.tvmaniac.i18n.MR.strings.logout
import com.thomaskioko.tvmaniac.i18n.MR.strings.trakt_dialog_logout_message
import com.thomaskioko.tvmaniac.i18n.MR.strings.trakt_dialog_logout_title
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.settings.presenter.DismissTraktDialog
import com.thomaskioko.tvmaniac.settings.presenter.SettingsActions
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.settings.presenter.ShowTraktDialog
import com.thomaskioko.tvmaniac.settings.presenter.TraktLoginClicked
import com.thomaskioko.tvmaniac.settings.presenter.TraktLogoutClicked
import com.thomaskioko.tvmaniac.settings.ui.SettingsGroup
import com.thomaskioko.tvmaniac.settings.ui.SettingsSectionLabel
import com.thomaskioko.tvmaniac.settings.ui.traktLoggedOutState
import com.thomaskioko.tvmaniac.settings.ui.traktState
import com.thomaskioko.tvmaniac.testtags.settings.SettingsTestTags

@Composable
internal fun TraktPage(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.testTag(SettingsTestTags.LIST_TEST_TAG)) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            SettingsGroup {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.trakt_logo),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        contentScale = ContentScale.Fit,
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = state.labels.traktTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = state.labels.traktDescription,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        item { SettingsSectionLabel(text = state.labels.traktAuthentication) }

        item {
            SettingsGroup {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = state.labels.traktConnected,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = state.labels.traktConnectedDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Button(
                        modifier = Modifier.testTag(SettingsTestTags.TRAKT_ACCOUNT_ROW_TEST_TAG),
                        enabled = !state.isProcessingTraktAuth,
                        onClick = {
                            onAction(if (state.isAuthenticated) ShowTraktDialog else TraktLoginClicked(AccountProvider.TRAKT))
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                        ),
                    ) {
                        if (state.isProcessingTraktAuth) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onSecondary,
                            )
                        } else {
                            Text(text = if (state.isAuthenticated) state.labels.logout else state.labels.login)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    LogoutDialog(
        isVisible = state.showTraktDialog,
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
        TvManiacAlertDialog(
            title = trakt_dialog_logout_title.resolve(context),
            message = trakt_dialog_logout_message.resolve(context),
            confirmButtonText = logout.resolve(context),
            dismissButtonText = label_settings_trakt_dialog_button_secondary.resolve(context),
            onConfirm = onLogoutClicked,
            onDismiss = onDismissDialog,
            confirmButtonTestTag = SettingsTestTags.LOGOUT_DIALOG_CONFIRM_BUTTON_TEST_TAG,
            dismissButtonTestTag = SettingsTestTags.LOGOUT_DIALOG_DISMISS_BUTTON_TEST_TAG,
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun TraktPageConnectedPreview() {
    TraktPage(
        state = traktState,
        onAction = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun TraktPageLoggedOutPreview() {
    TraktPage(
        state = traktLoggedOutState,
        onAction = {},
    )
}
