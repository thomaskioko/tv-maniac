package com.thomaskioko.tvmaniac.locale.implementation

import android.content.Context
import android.os.LocaleList
import com.thomaskioko.tvmaniac.core.base.di.ApplicationContext
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import java.util.Locale

@Inject
public actual class PlatformLocaleProvider(
    @ApplicationContext private val context: Context,
) {

    private val locale = MutableSharedFlow<String>(replay = 1)

    init {
        locale.tryEmit(Locale.getDefault().language)
    }

    public actual fun getCurrentLocale(): Flow<String> = locale

    public actual suspend fun setLocale(languageCode: String) {
        require(languageCode.isNotEmpty()) { "Language code cannot be empty" }

        val newLocale = Locale(languageCode)
        Locale.setDefault(newLocale)

        val configuration = context.resources.configuration

        val newLocales = LocaleList(newLocale)
        configuration.setLocales(newLocales)

        locale.emit(languageCode)
    }

    public actual fun getPreferredLocales(): Flow<List<String>> {
        val userLocales = userLocales()
        val defaultLocale = listOf(Locale.getDefault().language)

        return flowOf(if (userLocales.isNotEmpty()) userLocales.map { it.language }.sorted() else defaultLocale)
    }

    private fun userLocales(): List<Locale> {
        val locales = context.resources.configuration.locales
        return (0 until locales.size()).mapNotNull { index ->
            val javaLocale = locales.get(index)
            val language = javaLocale.language
            val country = javaLocale.country.toCountryOrNull()
            if (country != null) {
                Locale(language, country)
            } else {
                Locale(language)
            }
        }
    }

    private fun String.toCountryOrNull(): String? {
        return if (this.isNotEmpty() && this.length == 2) this else null
    }
}
