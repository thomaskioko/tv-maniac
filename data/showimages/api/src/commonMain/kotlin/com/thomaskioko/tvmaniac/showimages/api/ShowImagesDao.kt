package com.thomaskioko.tvmaniac.showimages.api

import com.thomaskioko.tvmaniac.core.db.SelectShowImages
import com.thomaskioko.tvmaniac.core.db.Show_image
import kotlinx.coroutines.flow.Flow

interface ShowImagesDao {

    fun insert(image: Show_image)

    fun observeShowImages(): Flow<List<SelectShowImages>>
}
