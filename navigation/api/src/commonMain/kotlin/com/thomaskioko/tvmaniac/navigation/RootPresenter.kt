package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.ShowFollowedNotifier
import kotlinx.coroutines.flow.StateFlow

public interface RootPresenter : ShowFollowedNotifier {
    public interface Factory {
        public operator fun invoke(
            componentContext: ComponentContext,
            navigator: RootNavigator,
        ): RootPresenter
    }

    public val childStack: StateFlow<ChildStack<*, RootChild>>

    public val childStackValue: Value<ChildStack<*, RootChild>>

    // TODO:: Scope-di-binding :: Instead of any we can create our own type PresenterWrapper<*>
    public val episodeSheetSlot: StateFlow<ChildSlot<*, Any>>

    public val episodeSheetSlotValue: Value<ChildSlot<*, Any>>

    public val themeState: StateFlow<ThemeState>

    public val themeStateValue: Value<ThemeState>

    public val notificationPermissionState: StateFlow<NotificationPermissionState>

    public val notificationPermissionStateValue: Value<NotificationPermissionState>

    override fun onShowFollowed()

    public fun onRationaleAccepted()

    public fun onRationaleDismissed()

    public fun onNotificationPermissionResult(granted: Boolean)

    public fun onDeepLink(destination: DeepLinkDestination)
}
