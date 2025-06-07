package com.thomaskioko.tvmaniac.locale.implementation

import app.cash.turbine.test
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

abstract class PlatformLocaleProviderTest {

    abstract val localeProvider: PlatformLocaleProvider

    @Test
    fun should_return_a_valid_locale_when_getCurrentLocale_is_called() = runTest {
        localeProvider.getCurrentLocale().test {
            awaitItem().shouldNotBeNull()
        }
    }

    @Test
    fun should_return_a_non_empty_list_when_getSupportedLocales_is_called() = runTest {
        localeProvider.getSupportedLocales().test {
            awaitItem().shouldNotBeEmpty()
            awaitComplete()
        }
    }

    @Test
    fun should_update_current_locale_when_setLocale_is_called() = runTest {
        val newLocale = "fr"

        localeProvider.setLocale(newLocale)
        localeProvider.getCurrentLocale().test {
            awaitItem() shouldBe newLocale
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_throw_exception_when_setLocale_is_called_with_empty_language_code() = runTest {
        val emptyLocale = ""

        assertFailsWith<IllegalArgumentException> {
            localeProvider.setLocale(emptyLocale)
        }
    }

    @Test
    fun should_update_to_latest_locale_when_setLocale_is_called_multiple_times() = runTest {
        val firstLocale = "fr"
        val secondLocale = "de"
        val thirdLocale = "it"

        localeProvider.setLocale(firstLocale)
        localeProvider.setLocale(secondLocale)
        localeProvider.setLocale(thirdLocale)

        localeProvider.getCurrentLocale().test {
            awaitItem() shouldBe thirdLocale
        }
    }

    @Test
    fun should_accept_valid_but_uncommon_locale_when_setLocale_is_called() = runTest {
        val uncommonLocale = "sw" // Swahili

        localeProvider.setLocale(uncommonLocale)

        localeProvider.getCurrentLocale().test {
            awaitItem() shouldBe uncommonLocale
            cancelAndIgnoreRemainingEvents()
        }
    }
}
