package com.thomaskioko.tvmaniac.showimages.api

import com.thomaskioko.tvmaniac.core.db.EmptyShowImage
import com.thomaskioko.tvmaniac.core.db.Show_image
import kotlinx.coroutines.flow.Flow

interface ShowImagesDao {

    fun upsert(images: List<Show_image>)

    fun upsert(image: Show_image)

    fun observeShowImages(): Flow<List<EmptyShowImage>>

    fun fetchShowImages(): List<EmptyShowImage>

    fun delete(id: Long)

    fun deleteAll()
}
