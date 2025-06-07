package com.thomaskioko.tvmaniac.locale.implementation

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class PlatformLocaleProviderIosTest : PlatformLocaleProviderTest() {

    override lateinit var localeProvider: PlatformLocaleProvider

    @BeforeTest
    fun setup() {
        localeProvider = PlatformLocaleProvider()
    }

    @Test
    fun `should emit the same value when getCurrentLocale is called multiple times`() = runTest {
        val firstCall = localeProvider.getCurrentLocale().first()
        val secondCall = localeProvider.getCurrentLocale().first()

        firstCall shouldBe secondCall
    }

    @Test
    fun `should return sorted list when getSupportedLocales is called`() = runTest {
        localeProvider.getSupportedLocales().test {
            val locales = awaitItem()

            val sortedLocales = locales.sorted()
            sortedLocales shouldBe locales

            awaitComplete()
        }
    }
}
