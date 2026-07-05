package com.thomaskioko.tvmaniac.debug.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.debug.presenter.DebugItem
import com.thomaskioko.tvmaniac.debug.presenter.DebugItemIcon
import com.thomaskioko.tvmaniac.debug.presenter.DebugItemRole
import com.thomaskioko.tvmaniac.debug.presenter.DebugState
import com.thomaskioko.tvmaniac.debug.presenter.OpenFeatureFlags
import com.thomaskioko.tvmaniac.debug.presenter.TriggerDebugNotification
import com.thomaskioko.tvmaniac.debug.presenter.TriggerDelayedDebugNotification
import com.thomaskioko.tvmaniac.debug.presenter.TriggerLibrarySync
import com.thomaskioko.tvmaniac.debug.presenter.TriggerTestCrash
import com.thomaskioko.tvmaniac.debug.presenter.TriggerUpNextSync
import com.thomaskioko.tvmaniac.subscription.api.AccountType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

internal class DebugStatePreviewParameterProvider : PreviewParameterProvider<DebugState> {
    override val values: Sequence<DebugState>
        get() = sequenceOf(
            defaultDebugState,
            accountTypePremiumState,
            accountTypeFreeState,
        )
}

private fun accountTypeSubtitle(override: AccountType): String = when (override) {
    AccountType.Premium -> "Premium"
    AccountType.Free -> "Free"
    AccountType.None -> "Update account type"
}

private fun previewItems(accountType: AccountType): ImmutableList<DebugItem> = listOf(
    DebugItem(
        id = "account_type",
        icon = DebugItemIcon.Account,
        title = "Account Type",
        subtitle = accountTypeSubtitle(accountType),
        action = null,
    ),
    DebugItem(
        id = "notifications",
        icon = DebugItemIcon.Notifications,
        title = "Episode Notifications",
        subtitle = "Schedule a test notification now",
        action = TriggerDebugNotification,
    ),
    DebugItem(
        id = "delayed-notification",
        icon = DebugItemIcon.Schedule,
        title = "Delayed Notification",
        subtitle = "Schedule a test notification in 5 minutes",
        action = TriggerDelayedDebugNotification,
    ),
    DebugItem(
        id = "library-sync",
        icon = DebugItemIcon.LibrarySync,
        title = "Library Sync",
        subtitle = "Last synced 2026-06-30 08:00",
        action = TriggerLibrarySync,
    ),
    DebugItem(
        id = "upnext-sync",
        icon = DebugItemIcon.UpNextSync,
        title = "UpNext Sync",
        subtitle = "Last synced 2026-06-30 08:00",
        action = TriggerUpNextSync,
    ),
    DebugItem(
        id = "feature-flags",
        icon = DebugItemIcon.FeatureFlags,
        title = "Feature Flags",
        subtitle = "Inspect and adjust remote feature flags",
        action = OpenFeatureFlags,
    ),
    DebugItem(
        id = "token-status",
        icon = DebugItemIcon.Key,
        title = "Token Status",
        subtitle = "Expires in 3d 4h",
        action = null,
    ),
    DebugItem(
        id = "test-crash",
        icon = DebugItemIcon.Warning,
        role = DebugItemRole.Destructive,
        title = "Test Crash",
        subtitle = "Trigger a test crash for Crashlytics",
        action = TriggerTestCrash,
    ),
).toImmutableList()

private fun debugState(accountType: AccountType): DebugState = DebugState(
    title = "Debug Menu",
    items = previewItems(accountType),
    isLoggedIn = true,
    accountType = accountType,
)

internal val defaultDebugState: DebugState = debugState(AccountType.None)
internal val accountTypePremiumState: DebugState = debugState(AccountType.Premium)
internal val accountTypeFreeState: DebugState = debugState(AccountType.Free)
