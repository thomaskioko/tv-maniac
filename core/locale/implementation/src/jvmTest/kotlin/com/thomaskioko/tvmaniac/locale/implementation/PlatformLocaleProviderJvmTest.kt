package com.thomaskioko.tvmaniac.locale.implementation

import kotlin.test.BeforeTest

internal class PlatformLocaleProviderJvmTest : PlatformLocaleProviderTest() {

    override lateinit var localeProvider: PlatformLocaleProvider

    @BeforeTest
    fun setup() {
        localeProvider = PlatformLocaleProvider()
    }
}
