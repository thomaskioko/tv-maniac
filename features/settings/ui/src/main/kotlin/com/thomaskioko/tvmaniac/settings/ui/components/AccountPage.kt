package com.thomaskioko.tvmaniac.settings.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.api.displayName
import com.thomaskioko.tvmaniac.android.designsystem.R
import com.thomaskioko.tvmaniac.compose.components.ProviderButton
import com.thomaskioko.tvmaniac.compose.components.ProviderSignInCard
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacAlertDialog
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_settings_trakt_dialog_button_secondary
import com.thomaskioko.tvmaniac.i18n.MR.strings.logout
import com.thomaskioko.tvmaniac.i18n.MR.strings.trakt_dialog_logout_message
import com.thomaskioko.tvmaniac.i18n.MR.strings.trakt_dialog_logout_title
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.settings.presenter.AccountLoginClicked
import com.thomaskioko.tvmaniac.settings.presenter.AccountLogoutClicked
import com.thomaskioko.tvmaniac.settings.presenter.ConfirmSwitchDiscard
import com.thomaskioko.tvmaniac.settings.presenter.DismissLogoutDialog
import com.thomaskioko.tvmaniac.settings.presenter.DismissSwitchDialog
import com.thomaskioko.tvmaniac.settings.presenter.SettingsActions
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.settings.presenter.ShowLogoutDialog
import com.thomaskioko.tvmaniac.settings.presenter.SwitchProviderClicked
import com.thomaskioko.tvmaniac.settings.ui.SettingsGroup
import com.thomaskioko.tvmaniac.settings.ui.accountLoggedOutState
import com.thomaskioko.tvmaniac.settings.ui.accountLoggingOutState
import com.thomaskioko.tvmaniac.settings.ui.accountState
import com.thomaskioko.tvmaniac.settings.ui.accountSwitchDialogState
import com.thomaskioko.tvmaniac.settings.ui.accountSwitchState
import com.thomaskioko.tvmaniac.settings.ui.accountSwitchingState
import com.thomaskioko.tvmaniac.testtags.settings.SettingsTestTags

@Composable
internal fun AccountPage(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.testTag(SettingsTestTags.LIST_TEST_TAG)) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            AccountSectionHeader(
                text = if (state.isAuthenticated) state.labels.traktAuthentication else state.labels.connectTitle,
                isLoading = state.isProcessingAuth,
            )
        }

        item { Spacer(modifier = Modifier.height(4.dp)) }

        if (state.isAuthenticated) {
            item { ConnectedAccountCard(state = state, onAction = onAction) }
        } else {
            item { ProviderSignIn(state = state, onAction = onAction) }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    LogoutDialog(
        isVisible = state.showLogoutConfirmation,
        onLogoutClicked = { onAction(AccountLogoutClicked) },
        onDismissDialog = { onAction(DismissLogoutDialog) },
    )

    SwitchProviderDialog(
        isVisible = state.showSwitchConfirmation,
        title = state.switchDialogTitle.orEmpty(),
        message = state.switchDialogMessage.orEmpty(),
        confirmText = state.labels.switchConfirm,
        cancelText = state.labels.switchCancel,
        onConfirm = { onAction(ConfirmSwitchDiscard) },
        onDismiss = { onAction(DismissSwitchDialog) },
    )
}

@Composable
private fun ConnectedAccountCard(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
) {
    SettingsGroup {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                state.activeProvider?.let { provider ->
                    Icon(
                        painter = painterResource(id = providerLogo(provider)),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(40.dp),
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = state.activeProvider?.displayName.orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = state.labels.traktConnected,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    state.accountConnectedDescription?.let { description ->
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    modifier = Modifier.testTag(SettingsTestTags.TRAKT_ACCOUNT_ROW_TEST_TAG),
                    enabled = !state.isProcessingAuth && !state.isSwitching,
                    onClick = { onAction(ShowLogoutDialog) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                    ),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text(text = state.labels.logout)
                }

                val switchTarget = state.switchTargetProvider
                if (switchTarget != null) {
                    SwitchProviderRow(
                        provider = switchTarget,
                        label = if (state.isSwitching) state.labels.switching else state.switchActionLabel.orEmpty(),
                        enabled = !state.isSwitching && !state.isProcessingAuth,
                        isSwitching = state.isSwitching,
                        onClick = { onAction(SwitchProviderClicked(switchTarget)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountSectionHeader(
    text: String,
    isLoading: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(14.dp)
                    .testTag(SettingsTestTags.AUTH_PROCESSING_INDICATOR_TEST_TAG),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
private fun ProviderSignIn(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
) {
    ProviderSignInCard(
        title = state.labels.traktAuthentication,
        description = state.labels.accountSyncDescription,
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        state.authProviders.forEach { option ->
            ProviderButton(
                text = option.label,
                logo = providerLogo(option.provider),
                onClick = { onAction(AccountLoginClicked(option.provider)) },
                enabled = !state.isProcessingAuth,
                modifier = if (option.provider == SyncProviderSource.TRAKT) {
                    Modifier.testTag(SettingsTestTags.TRAKT_ACCOUNT_ROW_TEST_TAG)
                } else {
                    Modifier
                },
            )
        }
    }
}

@Composable
private fun SwitchProviderRow(
    provider: SyncProviderSource,
    label: String,
    enabled: Boolean,
    isSwitching: Boolean,
    onClick: () -> Unit,
) {
    Button(
        modifier = Modifier.testTag(SettingsTestTags.SWITCH_PROVIDER_BUTTON_TEST_TAG),
        enabled = enabled,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
            contentColor = MaterialTheme.colorScheme.secondary,
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        if (isSwitching) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(16.dp)
                    .testTag(SettingsTestTags.SWITCHING_INDICATOR_TEST_TAG),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.secondary,
            )
        } else {
            Icon(
                painter = painterResource(id = providerLogo(provider)),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.secondary,
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = label)
    }
}

@Composable
private fun SwitchProviderDialog(
    isVisible: Boolean,
    title: String,
    message: String,
    confirmText: String,
    cancelText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(initialAlpha = 0.4f),
        exit = fadeOut(animationSpec = tween(durationMillis = 250)),
    ) {
        TvManiacAlertDialog(
            title = title,
            message = message,
            confirmButtonText = confirmText,
            dismissButtonText = cancelText,
            onConfirm = onConfirm,
            onDismiss = onDismiss,
            confirmButtonTestTag = SettingsTestTags.SWITCH_DIALOG_CONFIRM_BUTTON_TEST_TAG,
            dismissButtonTestTag = SettingsTestTags.SWITCH_DIALOG_DISMISS_BUTTON_TEST_TAG,
        )
    }
}

@DrawableRes
private fun providerLogo(provider: SyncProviderSource): Int = when (provider) {
    SyncProviderSource.TRAKT -> R.drawable.ic_trakt_mono
    SyncProviderSource.SIMKL -> R.drawable.ic_simkl_mono
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
private fun AccountPageConnectedPreview() {
    AccountPage(
        state = accountState,
        onAction = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun AccountPageLoggedOutPreview() {
    AccountPage(
        state = accountLoggedOutState,
        onAction = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun AccountPageSwitchAffordancePreview() {
    AccountPage(
        state = accountSwitchState,
        onAction = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun AccountPageSwitchDialogPreview() {
    AccountPage(
        state = accountSwitchDialogState,
        onAction = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun AccountPageSwitchingPreview() {
    AccountPage(
        state = accountSwitchingState,
        onAction = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun AccountPageLoggingOutPreview() {
    AccountPage(
        state = accountLoggingOutState,
        onAction = {},
    )
}
