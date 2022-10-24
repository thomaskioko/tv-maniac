package com.thomaskioko.tvmaniac.shared.domain.trailers.api

import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.model.Trailer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveTrailerInteractor constructor(
    private val trailerRepository: TrailerRepository
) : FlowInteractor<Int, List<Trailer>>() {

    override fun run(params: Int): Flow<List<Trailer>> =
        trailerRepository.observeTrailersByShowId(params)
            .map { it.toTrailerList() }

}

fun Resource<List<Trailers>>.toTrailerList(): List<Trailer> {
    return data?.map {
        Trailer(
            showId = it.trakt_id,
            key = it.key,
            name = it.name,
            youtubeThumbnailUrl = "https://i.ytimg.com/vi/${it.key}/hqdefault.jpg"
        )
    } ?: emptyList()
}