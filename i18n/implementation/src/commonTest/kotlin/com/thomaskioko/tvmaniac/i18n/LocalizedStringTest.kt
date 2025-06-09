package com.thomaskioko.tvmaniac.i18n

import com.thomaskioko.tvmaniac.i18n.StringResourceKey.ButtonErrorRetry
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import dev.icerock.moko.resources.desc.StringDesc
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

abstract class LocalizedStringTest {
    abstract val localizer: Localizer

    @Test
    fun should_return_english_string_for_default_locale() = runTest {
        StringDesc.localeType = StringDesc.LocaleType.Custom("en")

        val result = localizer.getString(ButtonErrorRetry)
        result shouldBe "Retry"
    }

    @Test
    fun should_return_french_string_for_fr_locale() = runTest {
        StringDesc.localeType = StringDesc.LocaleType.Custom("fr-FR")

        val result = localizer.getString(ButtonErrorRetry)
        result shouldBe "Réessayer"
    }

    @Test
    fun should_return_german_string_for_de_locale() = runTest {
        StringDesc.localeType = StringDesc.LocaleType.Custom("de-DE")

        val result = localizer.getString(ButtonErrorRetry)
        result shouldBe "Wiederholen"
    }

    @Test
    fun should_return_correct_string_when_locale_is_changed() = runTest {
        StringDesc.localeType = StringDesc.LocaleType.Custom("en")

        var result = localizer.getString(ButtonErrorRetry)
        result shouldBe "Retry"

        StringDesc.localeType = StringDesc.LocaleType.Custom("fr-FR")

        result = localizer.getString(ButtonErrorRetry)
        result shouldBe "Réessayer"

        StringDesc.localeType = StringDesc.LocaleType.Custom("de-DE")

        result = localizer.getString(ButtonErrorRetry)
        result shouldBe "Wiederholen"
    }
}
