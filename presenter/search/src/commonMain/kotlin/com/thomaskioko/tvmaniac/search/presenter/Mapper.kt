package com.thomaskioko.tvmaniac.search.presenter

import com.thomaskioko.tvmaniac.genre.ShowGenresEntity
import com.thomaskioko.tvmaniac.search.presenter.model.ShowGenre
import com.thomaskioko.tvmaniac.search.presenter.model.ShowItem
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.util.FormatterUtil
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Inject
class Mapper(
    private val formatterUtil: FormatterUtil,
) {

    fun toShowList(items: List<ShowEntity>): ImmutableList<ShowItem> =
        items.map {
            ShowItem(
                tmdbId = it.id,
                title = it.title,
                posterImageUrl = it.posterPath,
                inLibrary = it.inLibrary,
                status = it.status,
                voteAverage = it.voteAverage?.let { vote -> formatterUtil.formatDouble(vote, 1) },
                year = it.year,
                overview = it.overview,
            )
        }.toImmutableList()

    fun toGenreList(entities: List<ShowGenresEntity>): ImmutableList<ShowGenre> {
        return entities.map {
            ShowGenre(
                id = it.id,
                name = it.name,
                posterUrl = it.posterUrl,
            )
        }.toImmutableList()
    }
}
