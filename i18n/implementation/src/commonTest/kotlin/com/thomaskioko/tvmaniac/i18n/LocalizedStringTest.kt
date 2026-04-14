package com.thomaskioko.tvmaniac.i18n

import com.thomaskioko.tvmaniac.i18n.StringResourceKey.ButtonErrorRetry
import com.thomaskioko.tvmaniac.i18n.util.BaseResourceTests
import dev.icerock.moko.resources.desc.StringDesc
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test

class LocalizedStringTest : BaseResourceTests() {

    @AfterTest
    fun resetLocale() {
        StringDesc.localeType = StringDesc.LocaleType.System
    }

    @Test
    fun should_return_english_string_for_default_locale() = runTest {
        StringDesc.localeType = StringDesc.LocaleType.Custom("en")

        val result = localizer.getString(ButtonErrorRetry)
        result shouldBe "Retry"
    }

    @Test
    fun should_return_french_string_for_fr_locale() = runTest {
        StringDesc.localeType = StringDesc.LocaleType.Custom("fr")

        val result = localizer.getString(ButtonErrorRetry)
        result shouldBe "Réessayer"
    }

    @Test
    fun should_return_german_string_for_de_locale() = runTest {
        StringDesc.localeType = StringDesc.LocaleType.Custom("de")

        val result = localizer.getString(ButtonErrorRetry)
        result shouldBe "Wiederholen"
    }

    @Test
    fun should_return_correct_string_when_locale_is_changed() = runTest {
        StringDesc.localeType = StringDesc.LocaleType.Custom("en")

        var result = localizer.getString(ButtonErrorRetry)
        result shouldBe "Retry"

        StringDesc.localeType = StringDesc.LocaleType.Custom("fr")

        result = localizer.getString(ButtonErrorRetry)
        result shouldBe "Réessayer"

        StringDesc.localeType = StringDesc.LocaleType.Custom("de")

        result = localizer.getString(ButtonErrorRetry)
        result shouldBe "Wiederholen"
    }
}
