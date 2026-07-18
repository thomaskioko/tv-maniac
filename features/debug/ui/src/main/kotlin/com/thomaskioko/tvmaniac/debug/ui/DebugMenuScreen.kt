package com.thomaskioko.tvmaniac.debug.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.SnackBarStyle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.components.TvManiacSnackBarHost
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.debug.presenter.BackClicked
import com.thomaskioko.tvmaniac.debug.presenter.DebugActions
import com.thomaskioko.tvmaniac.debug.presenter.DebugItem
import com.thomaskioko.tvmaniac.debug.presenter.DebugItemIcon
import com.thomaskioko.tvmaniac.debug.presenter.DebugItemRole
import com.thomaskioko.tvmaniac.debug.presenter.DebugPresenter
import com.thomaskioko.tvmaniac.debug.presenter.DebugState
import com.thomaskioko.tvmaniac.debug.presenter.DismissSnackbar
import com.thomaskioko.tvmaniac.debug.presenter.SetAccountType
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_back
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_debug_account_type_description
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_debug_account_type_free
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_debug_account_type_premium
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_debug_account_type_title
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_ok
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.subscription.api.AccountType
import com.thomaskioko.tvmaniac.testtags.debug.DebugTestTags
import io.github.thomaskioko.codegen.annotations.ScreenUi

@ScreenUi(presenter = DebugPresenter::class, parentScope = ActivityScope::class)
@Composable
public fun DebugMenuScreen(
    presenter: DebugPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsState()

    DebugMenuScreen(
        state = state,
        onAction = { presenter.dispatch(it) },
        modifier = modifier,
    )
}

@Composable
internal fun DebugMenuScreen(
    state: DebugState,
    onAction: (DebugActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var showAccountTypeDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.testTag(DebugTestTags.SCREEN_TEST_TAG)) {
        Scaffold(
            topBar = {
                TvManiacTopBar(
                    navigationIcon = {
                        Icon(
                            modifier = Modifier
                                .clickable(onClick = { onAction(BackClicked) })
                                .padding(TvManiacSpacing.medium),
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = cd_back.resolve(context),
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    },
                    title = {
                        Text(
                            text = state.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = TvManiacSpacing.medium),
                        )
                    },
                    modifier = Modifier,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = MaterialTheme.colorScheme.background,
                    ),
                )
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag(DebugTestTags.LIST_TEST_TAG),
            ) {
                item { Spacer(modifier = Modifier.height(TvManiacSpacing.medium)) }

                itemsIndexed(
                    items = state.items,
                    key = { _, it -> it.id },
                ) { index, item ->
                    DebugMenuItem(
                        item = item,
                        onClick = {
                            if (item.id == ACCOUNT_TYPE_ITEM_ID) {
                                showAccountTypeDialog = true
                            } else {
                                item.action?.let(onAction)
                            }
                        },
                    )
                    if (index < state.items.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            modifier = Modifier.padding(horizontal = TvManiacSpacing.large),
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(TvManiacSpacing.xLarge)) }
            }
        }

        TvManiacSnackBarHost(
            message = state.message?.message,
            style = SnackBarStyle.Error,
            onDismiss = { state.message?.let { onAction(DismissSnackbar(it.id)) } },
        )
    }

    AccountTypeDialog(
        isVisible = showAccountTypeDialog,
        current = state.accountType,
        onOverrideSelected = { override ->
            onAction(SetAccountType(override))
            showAccountTypeDialog = false
        },
        onDismiss = { showAccountTypeDialog = false },
    )
}

@Composable
private fun DebugMenuItem(
    item: DebugItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val iconTint = when (item.role) {
        DebugItemRole.Accent -> MaterialTheme.colorScheme.secondary
        DebugItemRole.Destructive -> MaterialTheme.colorScheme.error
    }
    val isInteractive = item.action != null || item.id == ACCOUNT_TYPE_ITEM_ID
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (item.id == ACCOUNT_TYPE_ITEM_ID) Modifier.testTag(DebugTestTags.ACCOUNT_TYPE_ROW_TEST_TAG) else Modifier)
            .clickable(enabled = isInteractive && !item.isLoading, onClick = onClick)
            .padding(horizontal = TvManiacSpacing.medium, vertical = TvManiacSpacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = item.icon.toImageVector(),
            tint = iconTint,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )

        Spacer(modifier = Modifier.width(TvManiacSpacing.medium))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        when {
            item.isLoading -> CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.secondary,
            )
            isInteractive -> Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
internal fun AccountTypeDialog(
    isVisible: Boolean,
    current: AccountType,
    onOverrideSelected: (AccountType) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(initialAlpha = 0.4f),
        exit = fadeOut(animationSpec = tween(durationMillis = 250)),
    ) {
        AlertDialog(
            modifier = Modifier.testTag(DebugTestTags.ACCOUNT_TYPE_DIALOG_TEST_TAG),
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = label_debug_account_type_title.resolve(context),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            text = {
                Column {
                    Text(
                        text = label_debug_account_type_description.resolve(context),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = TvManiacSpacing.xSmall),
                    )
                    AccountTypeOption(
                        label = label_debug_account_type_premium.resolve(context),
                        selected = current == AccountType.Premium,
                        onClick = { onOverrideSelected(AccountType.Premium) },
                        modifier = Modifier.testTag(DebugTestTags.accountTypeOption(AccountType.Premium.name)),
                    )
                    AccountTypeOption(
                        label = label_debug_account_type_free.resolve(context),
                        selected = current == AccountType.Free,
                        onClick = { onOverrideSelected(AccountType.Free) },
                        modifier = Modifier.testTag(DebugTestTags.accountTypeOption(AccountType.Free.name)),
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = label_ok.resolve(context),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            },
        )
    }
}

@Composable
private fun AccountTypeOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(vertical = TvManiacSpacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

private const val ACCOUNT_TYPE_ITEM_ID = "account_type"

private fun DebugItemIcon.toImageVector(): ImageVector = when (this) {
    DebugItemIcon.Notifications -> Icons.Filled.Notifications
    DebugItemIcon.Schedule -> Icons.Filled.Schedule
    DebugItemIcon.LibrarySync -> Icons.Filled.Sync
    DebugItemIcon.UpNextSync -> Icons.Filled.Refresh
    DebugItemIcon.FeatureFlags -> Icons.Filled.Flag
    DebugItemIcon.Key -> Icons.Filled.VpnKey
    DebugItemIcon.Account -> Icons.Filled.Person
    DebugItemIcon.Warning -> Icons.Filled.Warning
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun DebugMenuScreenPreview(
    @PreviewParameter(DebugStatePreviewParameterProvider::class) state: DebugState,
) {
    DebugMenuScreen(
        state = state,
        onAction = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun AccountTypeDialogPreview() {
    AccountTypeDialog(
        isVisible = true,
        current = AccountType.Premium,
        onOverrideSelected = {},
        onDismiss = {},
    )
}
