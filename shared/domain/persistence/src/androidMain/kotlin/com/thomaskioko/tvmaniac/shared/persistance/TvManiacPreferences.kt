package com.thomaskioko.tvmaniac.shared.persistance

import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.runBlocking

const val KEY_THEME = "tvmaniac_preference"

actual class TvManiacPreferences actual constructor(
    private val sharedPreferences: Preference,
) {

    private val preferenceKeyChangedFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        preferenceKeyChangedFlow.tryEmit(key)
    }

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun emitTheme(themeValue: String) = runBlocking {
        sharedPreferences.edit().apply {
            putString(KEY_THEME, themeValue)
        }.apply()

        preferenceKeyChangedFlow.emit(KEY_THEME)
    }

    fun observeTheme(): Flow<Theme> {
        return preferenceKeyChangedFlow
            .onStart { emit(KEY_THEME) }
            .filter { it == KEY_THEME }
            .map {
                getThemeForStorageValue(
                    sharedPreferences.getString(KEY_THEME, "system")!!
                )
            }
            .distinctUntilChanged()
    }

    private fun getThemeForStorageValue(value: String) = when (value) {
        "light" -> Theme.LIGHT
        "dark" -> Theme.DARK
        else -> Theme.SYSTEM
    }
}
