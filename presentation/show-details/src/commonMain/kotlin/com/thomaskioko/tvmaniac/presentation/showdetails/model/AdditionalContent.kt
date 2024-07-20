package com.thomaskioko.tvmaniac.presentation.showdetails.model

import kotlinx.collections.immutable.ImmutableList

data class AdditionalContent(
  val similarShows: ImmutableList<Show>,
  val recommendedShows: ImmutableList<Show>,
  val trailers: ImmutableList<Trailer>,
)
