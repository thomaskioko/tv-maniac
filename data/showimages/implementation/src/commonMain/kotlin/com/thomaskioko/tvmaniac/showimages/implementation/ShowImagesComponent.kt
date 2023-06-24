package com.thomaskioko.tvmaniac.showimages.implementation

import com.thomaskioko.tvmaniac.showimages.api.ShowImagesDao
import com.thomaskioko.tvmaniac.showimages.api.ShowImagesRepository
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface ShowImagesComponent {

    @ApplicationScope
    @Provides
    fun provideShowImagesDao(bind: ShowImagesDaoImpl): ShowImagesDao = bind

    @ApplicationScope
    @Provides
    fun provideShowImagesRepository(bind: ShowImagesRepositoryImpl): ShowImagesRepository = bind
}
