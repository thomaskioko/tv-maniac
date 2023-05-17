package com.thomaskioko.tvmaniac.showimages.implementation

import com.thomaskioko.tvmaniac.showimages.api.ShowImagesDao
import com.thomaskioko.tvmaniac.showimages.api.ShowImagesRepository
import me.tatarka.inject.annotations.Provides

interface ShowImagesComponent {

    @Provides
    fun provideShowImagesDao(bind: ShowImagesDaoImpl): ShowImagesDao = bind

    @Provides
    fun provideShowImagesRepository(bind: ShowImagesRepositoryImpl): ShowImagesRepository = bind
}
