package com.thomaskioko.tvmaniac.i18n

import kotlin.test.BeforeTest

class MokoLocalizerIosTest : MokoLocalizerTest() {
    override lateinit var localizer: MokoResourcesLocalizer

    @BeforeTest
    fun setup() {
        localizer = MokoResourcesLocalizer(PlatformLocalizer())
    }
}
