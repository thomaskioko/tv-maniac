package com.thomaskioko.tvmaniac.datasource.repository.trailers

import com.thomaskioko.tvmaniac.presentation.model.TrailerModel

interface TrailerRepository {

    suspend fun getTrailers(showId: Int): List<TrailerModel>
}
