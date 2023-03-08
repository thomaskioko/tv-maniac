package com.thomaskioko.tvmaniac.core.util.di

import android.content.Context
import com.thomaskioko.tvmaniac.core.util.AppContext
import com.thomaskioko.tvmaniac.core.util.AppUtils
import com.thomaskioko.tvmaniac.core.util.helper.DateUtilHelper
import com.thomaskioko.tvmaniac.core.util.helper.DateUtilHelperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilModule {

    @Singleton
    @Provides
    fun provideDateUtilHelper(): DateUtilHelper = DateUtilHelperImpl()

    @Singleton
    @Provides
    fun provideAppUtils(
        @ApplicationContext context: Context
    ): AppUtils = AppUtils(context as AppContext)
}