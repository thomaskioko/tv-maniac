package com.thomaskioko.tvmaniac.tmdb.api

import com.thomaskioko.tvmaniac.core.db.Show_image
import kotlinx.coroutines.flow.Flow


interface ShowImageCache {

    fun insert(image: Show_image)

    fun observeShowArt(): Flow<List<Show_image>>
}