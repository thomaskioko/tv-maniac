package com.thomaskioko.tvmaniac.locale.implementation

import com.thomaskioko.tvmaniac.locale.api.LanguagePreference
import com.thomaskioko.tvmaniac.locale.api.LocaleProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultLocaleProvider(
    private val platformLocaleProvider: PlatformLocaleProvider,
    private val languagePreference: LanguagePreference,
) : LocaleProvider {

    override val currentLocale: Flow<String> = languagePreference.observeLanguage()

    override suspend fun setLocale(languageCode: String) {
        languagePreference.saveLanguage(languageCode)
    }

    override fun getPreferredLocales(): Flow<List<String>> {
        return platformLocaleProvider.getPreferredLocales()
    }
}
