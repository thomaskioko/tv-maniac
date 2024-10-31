package com.thomaskioko.tvmaniac.presentation.search

data class SearchResult(
  val tmdbId: Long = 0,
  val title: String = "",
  val status: String? = null,
  val posterImageUrl: String? = null,
  val overview: String? = null,
  val inLibrary: Boolean = false,
)
