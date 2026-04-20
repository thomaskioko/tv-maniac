package com.thomaskioko.tvmaniac.locale.testing

import com.thomaskioko.tvmaniac.locale.api.LocaleProvider
import com.thomaskioko.tvmaniac.locale.implementation.DefaultLocaleProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, replaces = [DefaultLocaleProvider::class])
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
