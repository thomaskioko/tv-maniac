package com.thomaskioko.tvmaniac.settings.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.thomaskioko.tvmaniac.compose.components.PremiumOverlay
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacAlertDialog
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.data.backup.api.BackupFormat
import com.thomaskioko.tvmaniac.i18n.MR
import com.thomaskioko.tvmaniac.settings.presenter.BackupDestinationCancelled
import com.thomaskioko.tvmaniac.settings.presenter.BackupDestinationSelected
import com.thomaskioko.tvmaniac.settings.presenter.BackupExportClicked
import com.thomaskioko.tvmaniac.settings.presenter.BackupImportCancelled
import com.thomaskioko.tvmaniac.settings.presenter.BackupImportClicked
import com.thomaskioko.tvmaniac.settings.presenter.BackupImportConfirmed
import com.thomaskioko.tvmaniac.settings.presenter.BackupImportConfirmedWithAccount
import com.thomaskioko.tvmaniac.settings.presenter.BackupRestoreConfirmationDialog
import com.thomaskioko.tvmaniac.settings.presenter.BackupRestoreSummary
import com.thomaskioko.tvmaniac.settings.presenter.BackupSourceCancelled
import com.thomaskioko.tvmaniac.settings.presenter.BackupSourceSelected
import com.thomaskioko.tvmaniac.settings.presenter.BackupSummaryDismissed
import com.thomaskioko.tvmaniac.settings.presenter.SettingsActions
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.settings.presenter.UpgradeToPremiumClicked
import com.thomaskioko.tvmaniac.settings.ui.BackupPreviewParameterProvider
import com.thomaskioko.tvmaniac.settings.ui.SettingsGroup
import com.thomaskioko.tvmaniac.settings.ui.SettingsGroupDivider
import com.thomaskioko.tvmaniac.settings.ui.SettingsNavigationRow
import com.thomaskioko.tvmaniac.testtags.settings.SettingsTestTags
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val BACKUP_PICKER_MIME_TYPE = "application/*"
private const val BACKUP_FILE_TIMESTAMP_PATTERN = "yyyyMMdd-HHmmss"

@Composable
internal fun BackupPage(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    val inInspectionMode = LocalInspectionMode.current

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(BACKUP_PICKER_MIME_TYPE),
    ) { uri ->
        if (uri != null) {
            onAction(BackupDestinationSelected(uri.toString()))
        } else {
            onAction(BackupDestinationCancelled)
        }
    }

    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) {
            onAction(BackupSourceSelected(uri.toString()))
        } else {
            onAction(BackupSourceCancelled)
        }
    }

    LaunchedEffect(state.backup.awaitingDestination) {
        if (state.backup.awaitingDestination && !inInspectionMode) {
            createDocumentLauncher.launch(backupFileName())
        }
    }

    LaunchedEffect(state.backup.awaitingSource) {
        if (state.backup.awaitingSource && !inInspectionMode) {
            openDocumentLauncher.launch(arrayOf(BACKUP_PICKER_MIME_TYPE))
        }
    }

    BackupPageContent(state = state, onAction = onAction, modifier = modifier)
}

@Composable
private fun BackupPageContent(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    val locked = state.premium.backupLocked
    val backup = state.backup
    val summary = backup.summary

    PremiumOverlay(
        locked = locked,
        badgeText = state.premium.badgeText,
        title = state.premium.backupLockedTitle,
        message = state.premium.backupLockedMessage,
        actionText = state.premium.upgradeText,
        onActionClick = { onAction(UpgradeToPremiumClicked) },
        modifier = modifier
            .fillMaxSize()
            .testTag(SettingsTestTags.BACKUP_LOCKED_TEST_TAG),
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize().testTag(SettingsTestTags.LIST_TEST_TAG)) {
            item { Spacer(modifier = Modifier.height(TvManiacSpacing.medium)) }

            item {
                SettingsGroup {
                    SettingsNavigationRow(
                        modifier = Modifier.testTag(SettingsTestTags.BACKUP_EXPORT_ROW_TEST_TAG),
                        icon = Icons.Filled.Backup,
                        title = backup.exportTitle,
                        description = backup.exportDescription,
                        enabled = !locked && !backup.isExporting,
                        isLoading = backup.isExporting,
                        loadingTestTag = SettingsTestTags.BACKUP_EXPORTING_INDICATOR_TEST_TAG,
                        onClick = { onAction(BackupExportClicked) },
                    )
                    SettingsGroupDivider()
                    SettingsNavigationRow(
                        modifier = Modifier.testTag(SettingsTestTags.BACKUP_IMPORT_ROW_TEST_TAG),
                        icon = Icons.Filled.SettingsBackupRestore,
                        title = backup.importTitle,
                        description = backup.importDescription,
                        enabled = !locked && !backup.isImporting,
                        isLoading = backup.isImporting,
                        loadingTestTag = SettingsTestTags.BACKUP_IMPORTING_INDICATOR_TEST_TAG,
                        onClick = { onAction(BackupImportClicked) },
                    )
                }
            }

            if (summary != null) {
                item {
                    BackupRestoreSummaryContent(
                        summary = summary,
                        onDismiss = { onAction(BackupSummaryDismissed) },
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(TvManiacSpacing.large)) }
        }
    }

    BackupRestoreConfirmDialog(
        confirm = backup.confirm,
        onAction = onAction,
    )
}

@Composable
private fun BackupRestoreConfirmDialog(
    confirm: BackupRestoreConfirmationDialog?,
    onAction: (SettingsActions) -> Unit,
) {
    when (confirm) {
        null -> Unit
        is BackupRestoreConfirmationDialog.Local -> TvManiacAlertDialog(
            title = confirm.title,
            message = confirm.message,
            confirmButtonText = confirm.confirmLabel,
            dismissButtonText = confirm.cancelLabel,
            onConfirm = { onAction(BackupImportConfirmed) },
            onDismiss = { onAction(BackupImportCancelled) },
            confirmButtonTestTag = SettingsTestTags.BACKUP_RESTORE_CONFIRM_BUTTON_TEST_TAG,
            dismissButtonTestTag = SettingsTestTags.BACKUP_RESTORE_DISMISS_BUTTON_TEST_TAG,
        )
        is BackupRestoreConfirmationDialog.Connected -> TvManiacAlertDialog(
            title = confirm.title,
            message = confirm.message,
            confirmButtonText = confirm.accountLabel,
            dismissButtonText = confirm.cancelLabel,
            onConfirm = { onAction(BackupImportConfirmedWithAccount) },
            onDismiss = { onAction(BackupImportCancelled) },
            confirmButtonTestTag = SettingsTestTags.BACKUP_RESTORE_CONFIRM_BUTTON_TEST_TAG,
            dismissButtonTestTag = SettingsTestTags.BACKUP_RESTORE_DISMISS_BUTTON_TEST_TAG,
            neutralButtonText = confirm.deviceLabel,
            onNeutral = { onAction(BackupImportConfirmed) },
            neutralButtonTestTag = SettingsTestTags.BACKUP_RESTORE_DEVICE_ONLY_BUTTON_TEST_TAG,
        )
    }
}

@Composable
private fun BackupRestoreSummaryContent(
    summary: BackupRestoreSummary,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag(SettingsTestTags.BACKUP_RESTORE_SUMMARY_TEST_TAG),
    ) {
        Spacer(modifier = Modifier.height(TvManiacSpacing.medium))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = TvManiacSpacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = summary.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            IconButton(
                modifier = Modifier.testTag(SettingsTestTags.BACKUP_RESTORE_SUMMARY_DISMISS_BUTTON_TEST_TAG),
                onClick = onDismiss,
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(MR.strings.cd_dismiss.resourceId),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(TvManiacSpacing.medium))

        SettingsGroup {
            Column(modifier = Modifier.padding(TvManiacSpacing.medium)) {
                Text(
                    text = summary.showsRestored,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(TvManiacSpacing.xxSmall))
                Text(
                    text = summary.episodesRestored,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        val showsSkipped = summary.showsSkipped
        if (showsSkipped != null) {
            Spacer(modifier = Modifier.height(TvManiacSpacing.medium))
            SettingsGroup {
                Column(modifier = Modifier.padding(TvManiacSpacing.medium)) {
                    Text(
                        text = showsSkipped,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    summary.skippedShows.forEach { name ->
                        Spacer(modifier = Modifier.height(TvManiacSpacing.xxSmall))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        val rewatchNotice = summary.rewatchNotice
        if (rewatchNotice != null) {
            Spacer(modifier = Modifier.height(TvManiacSpacing.medium))
            Text(
                text = rewatchNotice,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = TvManiacSpacing.medium),
            )
        }

        Spacer(modifier = Modifier.height(TvManiacSpacing.large))
    }
}

private fun backupFileName(): String {
    val timestamp = SimpleDateFormat(BACKUP_FILE_TIMESTAMP_PATTERN, Locale.US).format(Date())
    return "${BackupFormat.FILE_PREFIX}$timestamp${BackupFormat.FILE_EXTENSION}"
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun BackupPagePreview(
    @PreviewParameter(BackupPreviewParameterProvider::class) state: SettingsState,
) {
    BackupPageContent(
        state = state,
        onAction = {},
    )
}
