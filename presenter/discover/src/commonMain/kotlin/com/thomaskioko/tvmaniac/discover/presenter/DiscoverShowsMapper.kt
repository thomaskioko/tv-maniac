package com.thomaskioko.tvmaniac.discover.presenter

import com.thomaskioko.tvmaniac.discover.presenter.model.DiscoverShow
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

internal fun List<ShowEntity>?.toShowList(): ImmutableList<DiscoverShow> =
    this?.map {
        DiscoverShow(
            tmdbId = it.id,
            title = it.title,
            posterImageUrl = it.posterPath,
            inLibrary = it.inLibrary,
            overView = it.overview,
        )
    }
        ?.toImmutableList()
        ?: persistentListOf()
