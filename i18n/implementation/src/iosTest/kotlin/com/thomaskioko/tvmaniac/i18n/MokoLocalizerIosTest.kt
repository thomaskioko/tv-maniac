package com.thomaskioko.tvmaniac.i18n

import kotlin.test.BeforeTest
import kotlin.test.Ignore

@Ignore
class MokoLocalizerIosTest : MokoLocalizerTest() {
    override lateinit var localizer: MokoResourcesLocalizer

    @BeforeTest
    fun setup() {
        localizer = MokoResourcesLocalizer(PlatformLocalizer())
    }
}
