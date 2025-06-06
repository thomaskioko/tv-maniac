package com.thomaskioko.tvmaniac.locale.implementation

import com.thomaskioko.tvmaniac.locale.api.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import me.tatarka.inject.annotations.Inject
import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.availableLocaleIdentifiers
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.Foundation.localizedStringForLanguageCode

@Inject
public actual class PlatformLocaleProvider {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    private val locale = MutableSharedFlow<String>(replay = 1)

    init {
        val savedLocale = userDefaults.stringForKey(LOCALE_KEY)

        val localeCode = if (savedLocale.isNullOrEmpty()) {
            getSystemLocale()
        } else if (savedLocale.contains("_")) {
            savedLocale.split("_")[0]
        } else {
            savedLocale
        }

        val appleLocaleCode = "${localeCode}_${localeCode.uppercase()}"
        userDefaults.setObject(listOf(appleLocaleCode), "AppleLanguages")
        userDefaults.synchronize()

        locale.tryEmit(localeCode)
    }

    public actual fun getCurrentLocale(): Flow<String> = locale

    public actual suspend fun setLocale(languageCode: String) {
        require(languageCode.isNotEmpty()) { "Language code cannot be empty" }

        val simpleLanguageCode = if (languageCode.contains("_")) {
            languageCode.split("_")[0]
        } else {
            languageCode
        }

        val appleLocaleCode = "${simpleLanguageCode}_${simpleLanguageCode.uppercase()}"

        userDefaults.setObject(simpleLanguageCode, LOCALE_KEY)

        userDefaults.setObject(listOf(appleLocaleCode), "AppleLanguages")
        userDefaults.synchronize()

        locale.emit(simpleLanguageCode)
    }

    public actual fun getSupportedLocales(): Flow<List<String>> {
        val languageCodes = mutableSetOf<String>()

        val availableIdentifiers = NSLocale.availableLocaleIdentifiers

        availableIdentifiers.forEach { item ->
            if (item is String) {
                val languageCode = item.split('_', '-')[0].lowercase()
                if (languageCode.isNotEmpty()) {
                    languageCodes.add(languageCode)
                }
            }
        }

        return flowOf(languageCodes.toList().sorted())
    }

    public actual suspend fun getLanguageFromCode(code: String): Language {
        val locale = NSLocale.currentLocale

        // Extract the language code from the locale code (e.g., "fr_FR" -> "fr")
        val languageCode = if (code.contains("_")) {
            code.split("_")[0]
        } else {
            code
        }

        val displayName = locale.localizedStringForLanguageCode(languageCode) ?: languageCode

        return Language(
            code = languageCode,
            displayName = displayName.capitalize(),
        )
    }

    private fun getSystemLocale(): String {
        return NSLocale.currentLocale.languageCode ?: "en"
    }

    private fun String.capitalize(): String {
        return if (this.isNotEmpty()) {
            this[0].uppercase() + this.substring(1)
        } else {
            this
        }
    }

    internal companion object {
        private const val LOCALE_KEY = "preferred_locale"
    }
}
