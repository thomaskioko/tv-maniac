package com.thomaskioko.tvmaniac.debug.presenter

import com.thomaskioko.tvmaniac.subscription.api.AccountType

public sealed interface DebugActions

public data object BackClicked : DebugActions

public data object TriggerDebugNotification : DebugActions

public data object TriggerDelayedDebugNotification : DebugActions

public data object TriggerLibrarySync : DebugActions

public data object TriggerUpNextSync : DebugActions

public data class DismissSnackbar(val messageId: Long) : DebugActions

public data object OpenFeatureFlags : DebugActions

public data object TriggerTestCrash : DebugActions

public data class SetAccountType(val accountType: AccountType) : DebugActions
