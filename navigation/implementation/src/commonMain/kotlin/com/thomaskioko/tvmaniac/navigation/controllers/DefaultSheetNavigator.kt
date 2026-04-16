package com.thomaskioko.tvmaniac.navigation.controllers

import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.dismiss
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.navigation.SheetConfig
import com.thomaskioko.tvmaniac.navigation.SheetNavigator
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class)
public class DefaultSheetNavigator : SheetNavigator {
    private val slotNavigation = SlotNavigation<SheetConfig>()

    override fun activate(config: SheetConfig) {
        slotNavigation.activate(config)
    }

    override fun dismiss() {
        slotNavigation.dismiss()
    }

    override fun getSlotNavigation(): SlotNavigation<SheetConfig> = slotNavigation
}
