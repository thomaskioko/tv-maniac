package com.thomaskioko.tvmaniac.settings.domain

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.thomaskioko.tvmaniac.presentation.contract.Theme
import com.thomaskioko.tvmaniac.settings.R
import com.thomaskioko.tvmaniac.settings.api.TvManiacPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Named

class TvManiacPreferencesImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @Named("app") private val sharedPreferences: SharedPreferences
) : TvManiacPreferences {

    private val defaultThemeValue = context.getString(R.string.pref_theme_default_value)

    private val preferenceKeyChangedFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        preferenceKeyChangedFlow.tryEmit(key)
    }

    companion object {
        const val KEY_THEME = "pref_theme"
    }

    override fun setup() {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun emitTheme(themeValue: String) = runBlocking {
        sharedPreferences.edit {
            putString(KEY_THEME, themeValue)
        }
        preferenceKeyChangedFlow.emit(KEY_THEME)
    }

    override fun observeTheme(): Flow<Theme> {
        return preferenceKeyChangedFlow
            .onStart { emit(KEY_THEME) }
            .filter { it == KEY_THEME }
            .map {
                getThemeForStorageValue(
                    sharedPreferences.getString(KEY_THEME, defaultThemeValue)!!
                )
            }
            .distinctUntilChanged()
    }

    private fun getThemeForStorageValue(value: String) = when (value) {
        context.getString(R.string.pref_theme_light_value) -> Theme.LIGHT
        context.getString(R.string.pref_theme_dark_value) -> Theme.DARK
        else -> Theme.SYSTEM
    }
}
