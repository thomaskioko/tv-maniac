package com.thomaskioko.tvmaniac.locale.implementation

import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import platform.Foundation.NSBundle
import platform.Foundation.NSLocale
import platform.Foundation.preferredLanguages

@Inject
public actual class PlatformLocaleProvider {
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

    public actual fun getPreferredLocales(): Flow<List<String>> {
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

    private fun getSystemLocale(): String {
        return NSLocale.preferredLanguages().firstOrNull() as? String
            ?: NSBundle.mainBundle().preferredLocalizations().firstOrNull() as? String ?: "en"
    }
}
