package com.thomaskioko.tvmaniac.locale.implementation

import com.thomaskioko.tvmaniac.locale.api.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import me.tatarka.inject.annotations.Inject
import platform.Foundation.NSBundle
import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.currentLocale
import platform.Foundation.localizedStringForLanguageCode
import platform.Foundation.preferredLanguages

@Inject
public actual class PlatformLocaleProvider {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    private val locale = MutableSharedFlow<String>(replay = 1)

    init {
        val localeCode = getSystemLocale()
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

        locale.emit(simpleLanguageCode)
    }

    public actual fun getSupportedLocales(): Flow<List<String>> {
        val availableIdentifiers = NSLocale.preferredLanguages
            .filterIsInstance<String>()
            .mapNotNull { identifier ->
                // Language identifiers can be complex (e.g., "en-US", "zh-Hans-CN").
                // Splitting by '-' or '_' and taking the first part is a common way
                // to get the base language code (e.g., "en", "zh").
                identifier.split('-', '_').firstOrNull()?.lowercase()
            }
            .distinct()

        return flowOf(availableIdentifiers)
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
        return NSLocale.preferredLanguages().firstOrNull() as? String
            ?: NSBundle.mainBundle().preferredLocalizations().firstOrNull() as? String ?: "en"
    }

    private fun String.capitalize(): String {
        return if (this.isNotEmpty()) {
            this[0].uppercase() + this.substring(1)
        } else {
            this
        }
    }
}
