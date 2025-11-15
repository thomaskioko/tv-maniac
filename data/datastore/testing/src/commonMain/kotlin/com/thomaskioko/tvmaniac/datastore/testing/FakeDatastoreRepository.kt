package com.thomaskioko.tvmaniac.datastore.testing

import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.datastore.api.ListStyle
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeDatastoreRepository : DatastoreRepository {

    private val appThemeFlow = MutableStateFlow(AppTheme.SYSTEM_THEME)
    private val languageFlow: Channel<String> = Channel(Channel.UNLIMITED)
    private val listStyleFlow: Channel<ListStyle> = Channel(Channel.UNLIMITED)
    private val imageQualityFlow = MutableStateFlow(ImageQuality.MEDIUM)

    suspend fun setTheme(appTheme: AppTheme) {
        appThemeFlow.value = appTheme
    }

    suspend fun setLanguage(languageCode: String) {
        languageFlow.send(languageCode)
    }

    override fun saveTheme(appTheme: AppTheme) {
        appThemeFlow.value = appTheme
    }

    override fun observeTheme(): Flow<AppTheme> = appThemeFlow.asStateFlow()

    override suspend fun saveLanguage(languageCode: String) {
        // no -op
    }

    override fun observeLanguage(): Flow<String> = languageFlow.receiveAsFlow()

    override suspend fun saveListStyle(listStyle: ListStyle) {
        // no-op
    }

    override fun observeListStyle(): Flow<ListStyle> = listStyleFlow.receiveAsFlow()

    override suspend fun saveImageQuality(quality: ImageQuality) {
        imageQualityFlow.value = quality
    }

    override fun observeImageQuality(): Flow<ImageQuality> = imageQualityFlow.asStateFlow()
}
