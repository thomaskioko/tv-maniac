package com.thomaskioko.tvmaniac.presentation.watchlist.model

data class WatchlistItem(
  val tmdbId: Long,
  val title: String,
  val posterImageUrl: String? = null,
  val watchProgress: Float = 0F,
  val seasonCount: Int = 0,
  val episodeCount: Int = 0,
  val status: String? = null,
  val year:String? = null
)
