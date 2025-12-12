package com.thomaskioko.tvmaniac.datastore.implementation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
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
                AppTheme.TERMINAL_THEME.name -> AppTheme.TERMINAL_THEME
                AppTheme.AUTUMN_THEME.name -> AppTheme.AUTUMN_THEME
                AppTheme.AQUA_THEME.name -> AppTheme.AQUA_THEME
                AppTheme.AMBER_THEME.name -> AppTheme.AMBER_THEME
                AppTheme.SNOW_THEME.name -> AppTheme.SNOW_THEME
                AppTheme.CRIMSON_THEME.name -> AppTheme.CRIMSON_THEME
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
                ImageQuality.MEDIUM.name -> ImageQuality.MEDIUM
                ImageQuality.LOW.name -> ImageQuality.LOW
                else -> ImageQuality.MEDIUM
            }
        }

    override suspend fun saveOpenTrailersInYoutube(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_OPEN_TRAILERS_IN_YOUTUBE] = enabled
        }
    }

    override fun observeOpenTrailersInYoutube(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[KEY_OPEN_TRAILERS_IN_YOUTUBE] ?: false
        }

    override suspend fun saveIncludeSpecials(includeSpecials: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_INCLUDE_SPECIALS] = includeSpecials
        }
    }

    override fun observeIncludeSpecials(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[KEY_INCLUDE_SPECIALS] ?: false
        }

    companion object {
        val KEY_THEME = stringPreferencesKey("app_theme")
        val KEY_LANGUAGE = stringPreferencesKey("app_language")
        val KEY_LIST_STYLE = stringPreferencesKey("list_style")
        val KEY_IMAGE_QUALITY = stringPreferencesKey("image_quality")
        val KEY_OPEN_TRAILERS_IN_YOUTUBE = booleanPreferencesKey("open_trailers_in_youtube")
        val KEY_INCLUDE_SPECIALS = booleanPreferencesKey("include_specials")
    }
}
