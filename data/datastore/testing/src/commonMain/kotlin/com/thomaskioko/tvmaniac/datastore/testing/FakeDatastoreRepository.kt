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

public class FakeDatastoreRepository : DatastoreRepository {

    private val appThemeFlow = MutableStateFlow(AppTheme.SYSTEM_THEME)
    private val languageFlow: Channel<String> = Channel(Channel.UNLIMITED)
    private val listStyleFlow: Channel<ListStyle> = Channel(Channel.UNLIMITED)
    private val imageQualityFlow = MutableStateFlow(ImageQuality.MEDIUM)
    private val openTrailersInYoutubeFlow = MutableStateFlow(false)
    private val includeSpecialsFlow = MutableStateFlow(false)
    private val lastTraktUserId: MutableStateFlow<String?> = MutableStateFlow(null)

    public suspend fun setTheme(appTheme: AppTheme) {
        appThemeFlow.value = appTheme
    }

    public suspend fun setLastTraktUserId(userId: String?) {
        lastTraktUserId.value = userId
    }

    public suspend fun setLanguage(languageCode: String) {
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

    override suspend fun saveOpenTrailersInYoutube(enabled: Boolean) {
        openTrailersInYoutubeFlow.value = enabled
    }

    override fun observeOpenTrailersInYoutube(): Flow<Boolean> = openTrailersInYoutubeFlow.asStateFlow()

    override suspend fun saveIncludeSpecials(includeSpecials: Boolean) {
        includeSpecialsFlow.value = includeSpecials
    }

    override fun observeIncludeSpecials(): Flow<Boolean> = includeSpecialsFlow.asStateFlow()

    override suspend fun saveLastTraktUserId(userId: String?) {
    }

    override suspend fun getLastTraktUserId(): String? = lastTraktUserId.value
}
