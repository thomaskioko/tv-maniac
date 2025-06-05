package com.thomaskioko.tvmaniac.locale.testing

import com.thomaskioko.tvmaniac.locale.api.LocaleProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

public class FakeLocaleProvider(
    private var currentLocaleValue: String,
    private val supportedLocalesValue: List<String>,
) : LocaleProvider {
    private var setLocaleCalled = false
    private var lastSetLocale = ""

    override val currentLocale: Flow<String> = flowOf(currentLocaleValue)

    override suspend fun setLocale(languageCode: String) {
        setLocaleCalled = true
        lastSetLocale = languageCode
        currentLocaleValue = languageCode
    }

    override fun getSupportedLocales(): Flow<List<String>> = flowOf(supportedLocalesValue)
}
