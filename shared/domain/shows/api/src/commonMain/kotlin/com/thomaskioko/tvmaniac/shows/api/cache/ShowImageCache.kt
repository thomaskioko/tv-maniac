package com.thomaskioko.tvmaniac.shows.api.cache

import com.thomaskioko.tvmaniac.core.db.Show_image
import kotlinx.coroutines.flow.Flow


interface ShowImageCache {

    fun insert(image: Show_image)

    fun observeShowArt(traktId: Int): Flow<Show_image>
}