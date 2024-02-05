package com.thomaskioko.tvmaniac.presentation.watchlist.model

data class LibraryItem(
  val tmdbId: Long,
  val title: String,
  val posterImageUrl: String? = null,
)
