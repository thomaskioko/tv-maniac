package com.thomaskioko.tvmaniac.debug.presenter

public sealed interface DebugActions

public data object BackClicked : DebugActions

public data object TriggerDebugNotification : DebugActions

public data object TriggerDelayedDebugNotification : DebugActions

public data object TriggerLibrarySync : DebugActions

public data object TriggerUpNextSync : DebugActions

public data class DismissSnackbar(val messageId: Long) : DebugActions
