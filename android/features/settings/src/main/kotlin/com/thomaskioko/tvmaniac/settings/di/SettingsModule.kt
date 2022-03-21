package com.thomaskioko.tvmaniac.settings.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.thomaskioko.tvmaniac.shared.persistance.TvManiacPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object SettingsModule {
    @Named("app")
    @Provides
    @Singleton
    fun provideAppPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Singleton
    @Provides
    fun provideTvManiacPreferences(
        @Named("app") preference: SharedPreferences
    ): TvManiacPreferences = TvManiacPreferences(preference)
}
