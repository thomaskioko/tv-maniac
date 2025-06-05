package com.thomaskioko.tvmaniac.locale.implementation

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import me.tatarka.inject.annotations.Inject
import java.util.Locale

@Inject
public actual class PlatformLocaleProvider(
    private val context: Context,
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

        val resources = context.resources
        val configuration = resources.configuration

        configuration.setLocale(newLocale)

        @Suppress("DEPRECATION")
        resources.updateConfiguration(configuration, resources.displayMetrics)

        locale.emit(languageCode)
    }

    public actual fun getSupportedLocales(): Flow<List<String>> {
        val locales = Locale.getAvailableLocales()
            .map { it.language }
            .distinct()
            .filter { it.isNotEmpty() }
            .sorted()

        return flowOf(locales)
    }
}
