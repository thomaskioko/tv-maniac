package com.thomaskioko.tvmaniac.locale.implementation

import com.thomaskioko.tvmaniac.locale.api.Language
import com.thomaskioko.tvmaniac.locale.api.LocaleProvider
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultLocaleProvider(
    private val platformLocaleProvider: PlatformLocaleProvider,
) : LocaleProvider {

    override val currentLocale: Flow<String> = platformLocaleProvider.getCurrentLocale()

    override suspend fun setLocale(languageCode: String) {
        platformLocaleProvider.setLocale(languageCode)
    }

    override fun getSupportedLocales(): Flow<List<String>> {
        return platformLocaleProvider.getSupportedLocales()
    }

    override suspend fun getLanguageFromCode(code: String): Language {
        return platformLocaleProvider.getLanguageFromCode(code)
    }
}
