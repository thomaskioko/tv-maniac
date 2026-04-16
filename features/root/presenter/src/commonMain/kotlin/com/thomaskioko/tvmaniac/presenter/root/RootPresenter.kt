package com.thomaskioko.tvmaniac.presenter.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.thomaskioko.root.model.DeepLinkDestination
import com.thomaskioko.root.model.NotificationPermissionState
import com.thomaskioko.root.model.ThemeState
import com.thomaskioko.tvmaniac.navigation.RootChild
import com.thomaskioko.tvmaniac.navigation.SheetChild
import kotlinx.coroutines.flow.StateFlow

public interface RootPresenter {
    public interface Factory {
        public operator fun invoke(componentContext: ComponentContext): RootPresenter
    }

    public val childStack: StateFlow<ChildStack<*, RootChild>>

    public val childStackValue: Value<ChildStack<*, RootChild>>

    public val episodeSheetSlot: StateFlow<ChildSlot<*, SheetChild>>

    public val episodeSheetSlotValue: Value<ChildSlot<*, SheetChild>>

    public val themeState: StateFlow<ThemeState>

    public val themeStateValue: Value<ThemeState>

    public val notificationPermissionState: StateFlow<NotificationPermissionState>

    public val notificationPermissionStateValue: Value<NotificationPermissionState>

    public fun onRationaleAccepted()

    public fun onRationaleDismissed()

    public fun onNotificationPermissionResult(granted: Boolean)

    public fun onDeepLink(destination: DeepLinkDestination)
}
