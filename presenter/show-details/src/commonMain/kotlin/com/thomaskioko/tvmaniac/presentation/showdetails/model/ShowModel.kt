package com.thomaskioko.tvmaniac.presentation.showdetails.model

data class ShowModel(
  val tmdbId: Long,
  val title: String,
  val posterImageUrl: String?,
  val backdropImageUrl: String?,
  val isInLibrary: Boolean,
)
