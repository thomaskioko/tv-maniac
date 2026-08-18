package com.thomaskioko.tvmaniac.settings.ui.components

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.thomaskioko.tvmaniac.compose.components.PremiumOverlay
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.data.backup.api.BackupFormat
import com.thomaskioko.tvmaniac.settings.presenter.BackupDestinationCancelled
import com.thomaskioko.tvmaniac.settings.presenter.BackupDestinationSelected
import com.thomaskioko.tvmaniac.settings.presenter.BackupExportClicked
import com.thomaskioko.tvmaniac.settings.presenter.SettingsActions
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.settings.presenter.UpgradeToPremiumClicked
import com.thomaskioko.tvmaniac.settings.ui.BackupPreviewParameterProvider
import com.thomaskioko.tvmaniac.settings.ui.SettingsGroup
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
    val context = LocalContext.current
    val inInspectionMode = LocalInspectionMode.current

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(BACKUP_PICKER_MIME_TYPE),
    ) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
            )
            onAction(BackupDestinationSelected(uri.toString()))
        } else {
            onAction(BackupDestinationCancelled)
        }
    }

    LaunchedEffect(state.backup.awaitingDestination) {
        if (state.backup.awaitingDestination && !inInspectionMode) {
            createDocumentLauncher.launch(backupFileName())
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
                        title = state.backup.exportTitle,
                        description = state.backup.exportDescription,
                        enabled = !locked && !state.backup.isExporting,
                        isLoading = state.backup.isExporting,
                        loadingTestTag = SettingsTestTags.BACKUP_EXPORTING_INDICATOR_TEST_TAG,
                        onClick = { onAction(BackupExportClicked) },
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(TvManiacSpacing.large)) }
        }
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
