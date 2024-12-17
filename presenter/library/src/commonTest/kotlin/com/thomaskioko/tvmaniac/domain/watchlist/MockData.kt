package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.db.LibraryShows
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.presentation.watchlist.model.WatchlistItem
import kotlinx.collections.immutable.toPersistentList

val cachedResult =
  mutableListOf(
    LibraryShows(
      id = Id(84958),
      name = "Loki",
      poster_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
      status = "Ended",
      first_air_date = "2024"
    ),
  )

val uiResult =
  cachedResult
    .map {
      WatchlistItem(
        tmdbId = it.id.id,
        title = it.name,
        posterImageUrl = it.poster_path,
        status = it.status,
        year = it.first_air_date
      )
    }
    .toPersistentList()

val updatedData =
  listOf(
    LibraryShows(
      id = Id(84958),
      name = "Loki",
      poster_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
      status = "Ended",
      first_air_date = "2024"
    ),
    LibraryShows(
      id = Id(1232),
      name = "The Lazarus Project",
      poster_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
      status = "Ended",
      first_air_date = "2024"
    ),
  )

internal fun expectedUiResult(
  result: Either.Right<List<LibraryShows>> = Either.Right(updatedData)
) =
  result.right
    .map {
      WatchlistItem(
        tmdbId = it.id.id,
        title = it.name,
        posterImageUrl = it.poster_path,
        status = it.status,
        year = it.first_air_date
      )
    }
    .toPersistentList()
