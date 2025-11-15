package com.thomaskioko.tvmaniac.datastore.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.datastore.api.ListStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultDatastoreRepository(
    private val coroutineScope: AppCoroutineScope,
    private val dataStore: DataStore<Preferences>,
) : DatastoreRepository {

    override fun saveTheme(appTheme: AppTheme) {
        coroutineScope.io.launch {
            dataStore.edit { preferences -> preferences[KEY_THEME] = appTheme.name }
        }
    }

    override fun observeTheme(): Flow<AppTheme> =
        dataStore.data.map { preferences ->
            when (preferences[KEY_THEME]) {
                AppTheme.LIGHT_THEME.name -> AppTheme.LIGHT_THEME
                AppTheme.DARK_THEME.name -> AppTheme.DARK_THEME
                else -> AppTheme.SYSTEM_THEME
            }
        }

    override suspend fun saveLanguage(languageCode: String) {
        dataStore.edit { preferences ->
            preferences[KEY_LANGUAGE] = languageCode
        }
    }

    override fun observeLanguage(): Flow<String> =
        dataStore.data.map { preferences ->
            preferences[KEY_LANGUAGE] ?: "en"
        }

    override suspend fun saveListStyle(listStyle: ListStyle) {
        dataStore.edit { preferences ->
            preferences[KEY_LIST_STYLE] = listStyle.name
        }
    }

    override fun observeListStyle(): Flow<ListStyle> =
        dataStore.data.map { preferences ->
            when (preferences[KEY_LIST_STYLE]) {
                ListStyle.LIST.name -> ListStyle.LIST
                else -> ListStyle.GRID // Default to GRID
            }
        }

    override suspend fun saveImageQuality(quality: ImageQuality) {
        dataStore.edit { preferences ->
            preferences[KEY_IMAGE_QUALITY] = quality.name
        }
    }

    override fun observeImageQuality(): Flow<ImageQuality> =
        dataStore.data.map { preferences ->
            when (preferences[KEY_IMAGE_QUALITY]) {
                ImageQuality.HIGH.name -> ImageQuality.HIGH
                ImageQuality.LOW.name -> ImageQuality.LOW
                else -> ImageQuality.MEDIUM // Default
            }
        }

    companion object {
        val KEY_THEME = stringPreferencesKey("app_theme")
        val KEY_LANGUAGE = stringPreferencesKey("app_language")
        val KEY_LIST_STYLE = stringPreferencesKey("list_style")
        val KEY_IMAGE_QUALITY = stringPreferencesKey("image_quality")
    }
}
