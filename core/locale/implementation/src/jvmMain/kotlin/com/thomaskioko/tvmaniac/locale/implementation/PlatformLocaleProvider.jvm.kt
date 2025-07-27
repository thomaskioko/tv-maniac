package com.thomaskioko.tvmaniac.locale.implementation

import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import java.util.Locale

@Inject
public actual class PlatformLocaleProvider {

    private val locale = MutableSharedFlow<String>(replay = 1)

    init {
        locale.tryEmit(Locale.getDefault().language)
    }

    public actual fun getCurrentLocale(): Flow<String> {
        return locale
    }

    public actual suspend fun setLocale(languageCode: String) {
        require(languageCode.isNotEmpty()) { "Language code cannot be empty" }

        val newLocale = Locale.forLanguageTag(languageCode)
        Locale.setDefault(newLocale)

        locale.emit(languageCode)
    }

    public actual fun getPreferredLocales(): Flow<List<String>> {
        val locales = Locale.getAvailableLocales()
            .map { it.language }
            .distinct()
            .filter { it.isNotEmpty() }
            .sorted()

        return if (locales.isEmpty()) {
            val defaultLocale = Locale.getDefault()
            val defaultLocaleCode = "${defaultLocale.language}_${defaultLocale.country}"
            flowOf(listOf(defaultLocaleCode, "en_US"))
        } else {
            flowOf(locales)
        }
    }
}
