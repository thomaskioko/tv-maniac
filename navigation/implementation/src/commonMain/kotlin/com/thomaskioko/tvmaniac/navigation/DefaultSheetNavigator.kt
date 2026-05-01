package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class)
public class DefaultSheetNavigator(
    private val sheetConfigSerializer: SheetConfigSerializer,
) : SheetNavigator {
    private val slotNavigation = SlotNavigation<SheetConfig>()

    override fun activate(config: SheetConfig) {
        slotNavigation.activate(config)
    }

    override fun dismiss() {
        slotNavigation.dismiss()
    }

    override fun <T : Any> buildChildSlot(
        componentContext: ComponentContext,
        childFactory: (SheetConfig, ComponentContext) -> T,
    ): Value<ChildSlot<*, T>> = componentContext.childSlot(
        source = slotNavigation,
        key = SHEET_SLOT_KEY,
        serializer = sheetConfigSerializer.serializer,
        handleBackButton = true,
        childFactory = childFactory,
    )

    private companion object {
        const val SHEET_SLOT_KEY = "SheetSlotKey"
    }
}
