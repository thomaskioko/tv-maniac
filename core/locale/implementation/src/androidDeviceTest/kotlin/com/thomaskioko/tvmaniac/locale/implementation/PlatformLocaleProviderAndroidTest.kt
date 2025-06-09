package com.thomaskioko.tvmaniac.locale.implementation

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale

@RunWith(AndroidJUnit4::class)
internal class PlatformLocaleProviderAndroidTest : PlatformLocaleProviderTest() {

    private lateinit var context: Context

    override lateinit var localeProvider: PlatformLocaleProvider

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        localeProvider = PlatformLocaleProvider(context)
    }

    @Test
    fun should_contain_device_default_locale_when_getPreferredLocales_is_called() = runTest {
        val defaultLocale = Locale.getDefault().language

        localeProvider.getPreferredLocales().test {
            val supportedLocales = awaitItem()
            supportedLocales shouldContain defaultLocale
            awaitComplete()
        }
    }

    @Test
    fun should_persist_locale_between_instances_when_setLocale_is_called() = runTest {
        val newLocale = "es"

        localeProvider.setLocale(newLocale)

        val newLocaleProvider = PlatformLocaleProvider(context)
        newLocaleProvider.getCurrentLocale().test {
            awaitItem() shouldBe newLocale
        }
    }
}
