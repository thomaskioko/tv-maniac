package com.thomaskioko.tvmaniac.locale.testing

import com.thomaskioko.tvmaniac.locale.api.LanguagePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeLanguagePreference : LanguagePreference {
    private val languageFlow = MutableStateFlow("en")

    public fun setLanguageNow(languageCode: String) {
        languageFlow.value = languageCode
    }

    override fun observeLanguage(): Flow<String> = languageFlow.asStateFlow()

    override suspend fun saveLanguage(languageCode: String) {
        languageFlow.value = languageCode
    }
}
