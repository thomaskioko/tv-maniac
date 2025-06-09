package com.thomaskioko.tvmaniac.locale.testing

import com.thomaskioko.tvmaniac.locale.api.LocaleProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

public class FakeLocaleProvider(
    private val supportedLanguages: List<String> = listOf("en"),
) : LocaleProvider {
    private val _currentLocale = MutableStateFlow("en")

    override val currentLocale: Flow<String> = _currentLocale

    override suspend fun setLocale(languageCode: String) {
        _currentLocale.value = languageCode
    }

    override fun getPreferredLocales(): Flow<List<String>> = flowOf(supportedLanguages)
}
