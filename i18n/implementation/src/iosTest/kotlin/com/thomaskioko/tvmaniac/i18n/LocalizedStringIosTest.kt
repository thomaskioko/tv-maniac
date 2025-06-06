package com.thomaskioko.tvmaniac.i18n

import com.thomaskioko.tvmaniac.i18n.api.Localizer
import kotlin.test.BeforeTest

internal class LocalizedStringIosTest : LocalizedStringTest() {
    override lateinit var localizer: Localizer

    @BeforeTest
    fun setup() {
        localizer = MokoResourcesLocalizer(PlatformLocalizer())
    }
}
