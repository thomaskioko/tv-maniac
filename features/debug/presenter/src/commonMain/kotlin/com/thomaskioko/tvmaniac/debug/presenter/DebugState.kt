package com.thomaskioko.tvmaniac.debug.presenter

import com.thomaskioko.tvmaniac.core.view.UiMessage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class DebugState(
    val title: String = "",
    val items: ImmutableList<DebugItem> = persistentListOf(),
    val isLoggedIn: Boolean = false,
    val message: UiMessage? = null,
) {
    public companion object {
        public val DEFAULT_STATE: DebugState = DebugState()
    }
}

public data class DebugItem(
    val id: String,
    val icon: DebugItemIcon,
    val role: DebugItemRole = DebugItemRole.Accent,
    val title: String,
    val subtitle: String,
    val isLoading: Boolean = false,
    val action: DebugActions? = null,
)

public enum class DebugItemIcon {
    Notifications,
    Schedule,
    LibrarySync,
    UpNextSync,
    FeatureFlags,
    Key,
    Warning,
}

public enum class DebugItemRole {
    Accent,
    Destructive,
}
