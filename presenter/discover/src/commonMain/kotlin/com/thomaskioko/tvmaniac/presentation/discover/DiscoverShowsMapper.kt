package com.thomaskioko.tvmaniac.presentation.discover

import com.thomaskioko.tvmaniac.presentation.discover.model.DiscoverShow
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

fun List<ShowEntity>?.toShowList(): ImmutableList<DiscoverShow> =
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
