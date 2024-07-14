package com.thomaskioko.tvmaniac.presentation.showdetails.model

data class AdditionalContent(
  val similarShows: List<Show>,
  val recommendedShows: List<Show>,
  val trailers: List<Trailer>,
)
