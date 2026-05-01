package com.thomaskioko.tvmaniac.navigation.testing

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.navigation.SheetConfig
import com.thomaskioko.tvmaniac.navigation.SheetNavigator

public class FakeSheetNavigator : SheetNavigator {

    private val _activatedConfigs = mutableListOf<SheetConfig>()
    private var _dismissCount = 0

    public val activatedConfigs: List<SheetConfig> get() = _activatedConfigs.toList()

    public val dismissCount: Int get() = _dismissCount

    public val lastActivated: SheetConfig? get() = _activatedConfigs.lastOrNull()

    private val slotNavigation = SlotNavigation<SheetConfig>()

    override fun activate(config: SheetConfig) {
        _activatedConfigs += config
        slotNavigation.activate(config)
    }

    override fun dismiss() {
        _dismissCount++
        slotNavigation.dismiss()
    }

    override fun <T : Any> buildChildSlot(
        componentContext: ComponentContext,
        childFactory: (SheetConfig, ComponentContext) -> T,
    ): Value<ChildSlot<*, T>> = componentContext.childSlot(
        source = slotNavigation,
        serializer = null,
        key = "FakeSheetSlotKey",
        handleBackButton = true,
        childFactory = childFactory,
    )
}

/**
 * Returns [FakeSheetNavigator.lastActivated] cast to [T], or fails with a clear message if nothing
 * has been activated or the last config was a different type. Useful when a test cares about one
 * feature's config shape, e.g. `sheetNavigator.lastActivatedAs<EpisodeSheetConfig>().episodeId shouldBe 42`.
 */
public inline fun <reified T : SheetConfig> FakeSheetNavigator.lastActivatedAs(): T {
    val last = lastActivated
        ?: error("Expected a ${T::class.simpleName} to have been activated, but activatedConfigs is empty")
    check(last is T) {
        "Expected last activated config to be ${T::class.simpleName} but was ${last::class.simpleName}"
    }
    return last
}
