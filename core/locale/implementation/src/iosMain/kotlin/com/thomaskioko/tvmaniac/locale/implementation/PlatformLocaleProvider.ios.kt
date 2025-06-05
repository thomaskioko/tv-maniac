package com.thomaskioko.tvmaniac.locale.implementation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import me.tatarka.inject.annotations.Inject
import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.Foundation.preferredLanguages

@Inject
public actual class PlatformLocaleProvider {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    private val locale = MutableSharedFlow<String>(replay = 1)

    init {
        val savedLocale = userDefaults.stringForKey(LOCALE_KEY) ?: getSystemLocale()
        locale.tryEmit(savedLocale)
    }

    public actual fun getCurrentLocale(): Flow<String> = locale

    public actual suspend fun setLocale(languageCode: String) {
        require(languageCode.isNotEmpty()) { "Language code cannot be empty" }

        userDefaults.setObject(languageCode, LOCALE_KEY)
        userDefaults.synchronize()

        locale.emit(languageCode)
    }

    public actual fun getSupportedLocales(): Flow<List<String>> {
        val preferredLanguages = NSLocale.preferredLanguages as List<String>

        val languageCodes = preferredLanguages
            .mapNotNull { localeId ->
                val components = localeId.split("-", "_")
                if (components.isNotEmpty()) components[0] else null
            }.distinct().sorted()

        val defaultLocale = listOf(getSystemLocale())
        val locales = languageCodes.takeIf { it.isNotEmpty() } ?: defaultLocale

        return flowOf(locales)
    }

    private fun getSystemLocale(): String {
        return NSLocale.currentLocale.languageCode ?: "en"
    }

    internal companion object {
        private const val LOCALE_KEY = "preferred_locale"
    }
}
