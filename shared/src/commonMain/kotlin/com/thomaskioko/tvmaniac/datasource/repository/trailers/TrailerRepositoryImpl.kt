package com.thomaskioko.tvmaniac.datasource.repository.trailers

import com.thomaskioko.tvmaniac.datasource.cache.SelectByShowId
import com.thomaskioko.tvmaniac.datasource.cache.Trailers
import com.thomaskioko.tvmaniac.datasource.cache.trailers.TrailerCache
import com.thomaskioko.tvmaniac.datasource.network.api.TvShowsService
import com.thomaskioko.tvmaniac.datasource.network.model.TrailerResponse
import com.thomaskioko.tvmaniac.presentation.model.TrailerModel

class TrailerRepositoryImpl(
    private val apiService: TvShowsService,
    private val cache: TrailerCache
) : TrailerRepository {

    override suspend fun getTrailers(showId: Int): List<TrailerModel> {
        return if (cache.getTrailers(showId).isEmpty()) {

            val trailerEntity = apiService.getTrailers(showId)
                .results.toCacheList(showId)

            cache.insert(trailerEntity)

            cache.getTrailers(showId)
                .toTrailerModelList()
        } else {
            cache.getTrailers(showId)
                .toTrailerModelList()
        }
    }

    private fun List<TrailerResponse>.toCacheList(showId: Int): List<Trailers> {
        return map { response ->
            Trailers(
                id = response.id.toLong(),
                show_id = showId.toLong(),
                key = response.key,
                name = response.name,
                site = response.site,
                size = response.size.toLong(),
                type = response.type
            )
        }
    }

    private fun List<SelectByShowId>.toTrailerModelList(): List<TrailerModel> {
        return map { cache ->
            TrailerModel(
                id = cache.id,
                youtubeKey = cache.key,
                title = cache.name,
                trailerProvider = cache.site,
                trailerResolution = cache.size,
                videoType = cache.type
            )
        }
    }
}
