package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.db.LibraryShows
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.presentation.watchlist.model.LibraryItem
import kotlinx.collections.immutable.toPersistentList

val cachedResult =
  mutableListOf(
    LibraryShows(
      id = Id(84958),
      name = "Loki",
      poster_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
      backdrop_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
      created_at = 12345645,
    ),
  )

val uiResult =
  cachedResult
    .map {
      LibraryItem(
        tmdbId = it.id.id,
        title = it.name,
        posterImageUrl = it.poster_path,
      )
    }
    .toPersistentList()

val updatedData =
  listOf(
    LibraryShows(
      id = Id(84958),
      name = "Loki",
      poster_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
      backdrop_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
      created_at = 12345645,
    ),
    LibraryShows(
      id = Id(1232),
      name = "The Lazarus Project",
      poster_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
      backdrop_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
      created_at = 12345645,
    ),
  )

internal fun expectedUiResult(
  result: Either.Right<List<LibraryShows>> = Either.Right(updatedData)
) =
  result.right
    .map {
      LibraryItem(
        tmdbId = it.id.id,
        title = it.name,
        posterImageUrl = it.poster_path,
      )
    }
    .toPersistentList()
