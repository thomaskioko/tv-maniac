package com.thomaskioko.tvmaniac.locale.implementation

import com.thomaskioko.tvmaniac.locale.api.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import me.tatarka.inject.annotations.Inject
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

    public actual fun getSupportedLocales(): Flow<List<String>> {
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

    public actual suspend fun getLanguageFromCode(code: String): Language {
        val locale = if (code.contains("_")) {
            val parts = code.split("_")
            if (parts.size >= 2) {
                Locale(parts[0], parts[1])
            } else {
                Locale(parts[0])
            }
        } else {
            Locale(code)
        }

        return Language(
            code = code,
            displayName = locale.displayLanguage.capitalize(),
        )
    }

    private fun String.capitalize(): String {
        return if (this.isNotEmpty()) {
            this[0].uppercase() + this.substring(1)
        } else {
            this
        }
    }
}
